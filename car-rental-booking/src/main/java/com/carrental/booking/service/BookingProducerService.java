package com.carrental.booking.service;

import com.carrental.dto.BookingDto;
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

    @Value("${kafka.saved-booking-producer-topic-name}")
    private String savedBookingProducerTopicName;

    @Value("${kafka.updated-booking-producer-topic-name}")
    private String updatedBookingProducerTopicName;

    @Value("${kafka.deleted-booking-producer-topic-name}")
    private String deletedBookingProducerTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSavedBooking(BookingDto bookingDto) {
        kafkaTemplate.send(buildMessage(bookingDto, savedBookingProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent message=[" + bookingDto + "] with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    log.error("Unable to send message=[" + bookingDto + "] due to : " + e.getMessage());
                });
    }

    public void sendUpdatedBooking(BookingDto bookingDto) {
        kafkaTemplate.send(buildMessage(bookingDto, updatedBookingProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent message=[" + bookingDto + "] with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    log.error("Unable to send message=[" + bookingDto + "] due to : " + e.getMessage());
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

                    log.error("Unable to send id=[" + bookingId + "] for deleted booking due to : " + e.getMessage());
                });
    }

    private <T> Message<T> buildMessage(T t, String topicName) {
        return MessageBuilder.withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
