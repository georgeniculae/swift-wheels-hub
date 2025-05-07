package com.autohub.booking.producer.bookingprocessing;

import com.autohub.dto.common.BookingResponse;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.savedBookingProducer-out-0.destination}")
    private String savedBookingProducerTopicName;

    @Value("${spring.cloud.stream.bindings.updatedBookingProducer-out-0.destination}")
    private String updatedBookingProducerTopicName;

    @Value("${spring.cloud.stream.bindings.deletedBookingProducer-out-0.destination}")
    private String deletedBookingProducerTopicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "debeziumListener"
    )
    public void sendSavedBooking(BookingResponse bookingResponse) {
        try {
            kafkaTemplate.send(buildMessage(bookingResponse, savedBookingProducerTopicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            logSentMessage(bookingResponse, result);
                        }
                    })
                    .join();
        } catch (Exception e) {
            throw new AutoHubException("Unable to send created booking: " + bookingResponse + " due to : " + e.getMessage());
        }
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "debeziumListener"
    )
    public void sendUpdatedBooking(BookingResponse bookingResponse) {
        try {
            kafkaTemplate.send(buildMessage(bookingResponse, updatedBookingProducerTopicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            logSentMessage(bookingResponse, result);
                        }
                    })
                    .join();
        } catch (Exception e) {
            throw new AutoHubException("Unable to send updated booking: " + bookingResponse + " due to : " + e.getMessage());
        }
    }

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "debeziumListener"
    )
    public void sendDeletedBooking(Long bookingId) {
        try {
            kafkaTemplate.send(buildMessage(bookingId, deletedBookingProducerTopicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            log.info(
                                    "Sent id=[{}] for deleted booking with offset=[{}]",
                                    bookingId,
                                    result.getRecordMetadata().offset()
                            );
                        }
                    })
                    .join();
        } catch (Exception e) {
            throw new AutoHubException("Unable to send id=[" + bookingId + "] for deleted booking due to : " + e.getMessage());
        }
    }

    private <T> Message<T> buildMessage(T t, String topicName) {
        return MessageBuilder.withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

    private void logSentMessage(BookingResponse bookingResponse, SendResult<String, Object> result) {
        log.info("Sent message: {} with offset: {}", bookingResponse, result.getRecordMetadata().offset());
    }

}
