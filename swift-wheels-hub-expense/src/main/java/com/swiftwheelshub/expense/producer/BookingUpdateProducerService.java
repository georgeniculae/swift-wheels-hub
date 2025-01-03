package com.swiftwheelshub.expense.producer;

import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingUpdateProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.bookingUpdateProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = {"invoiceService", "invoiceReprocessingService"}
    )
    public boolean closeBooking(BookingClosingDetails bookingClosingDetails) {
        try {
            kafkaTemplate.send(buildMessage(bookingClosingDetails, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            log.info(
                                    "Sent booking closing details: {} with offset: {}",
                                    bookingClosingDetails,
                                    result.getRecordMetadata().offset()
                            );

                            return;
                        }

                        log.error("Unable to send message: {} due to : {}", bookingClosingDetails, e.getMessage());
                    })
                    .join();

            return true;
        } catch (Exception e) {
            throw new SwiftWheelsHubException("Error while closing booking: " + bookingClosingDetails + " " + e.getMessage());
        }
    }

    @Recover
    public boolean recover(Exception e, BookingClosingDetails bookingClosingDetails) {
        log.error("Error after re-trying to close booking: {}: {}", bookingClosingDetails, e.getMessage(), e);

        return false;
    }

    private Message<BookingClosingDetails> buildMessage(BookingClosingDetails bookingClosingDetails, String topicName) {
        return MessageBuilder.withPayload(bookingClosingDetails)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
