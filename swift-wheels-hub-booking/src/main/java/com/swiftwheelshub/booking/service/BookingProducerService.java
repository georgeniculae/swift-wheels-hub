package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingProducerService {

    @Value("${spring.cloud.stream.bindings.savedBookingProducer-out-0.destination}")
    private String savedBookingProducerTopicName;

    @Value("${spring.cloud.stream.bindings.updatedBookingProducer-out-0.destination}")
    private String updatedBookingProducerTopicName;

    @Value("${spring.cloud.stream.bindings.deletedBookingProducer-out-0.destination}")
    private String deletedBookingProducerTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSavedBooking(BookingResponse bookingResponse) {
        kafkaTemplate.send(buildMessage(bookingResponse, savedBookingProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent message=[" + bookingResponse + "] with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    throw new SwiftWheelsHubException("Unable to send message=[" + bookingResponse + "] due to : " + e.getMessage());
                });
    }

    public void sendUpdatedBooking(BookingResponse bookingResponse) {
        kafkaTemplate.send(buildMessage(bookingResponse, updatedBookingProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent message=[" + bookingResponse + "] with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    throw new SwiftWheelsHubException("Unable to send message=[" + bookingResponse + "] due to : " + e.getMessage());
                });
    }

    public void sendDeletedBooking(Long bookingId) {
        kafkaTemplate.send(buildMessage(bookingId, deletedBookingProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent id=[" + bookingId + "] for deleted booking with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    throw new SwiftWheelsHubException("Unable to send id=[" + bookingId + "] for deleted booking due to : " + e.getMessage());
                });
    }

    private <T> Message<T> buildMessage(T t, String topicName) {
        return MessageBuilder.withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
