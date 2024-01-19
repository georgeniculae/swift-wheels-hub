package com.swiftwheelshub.booking.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingProducerTopicConfig {

    @Value("${kafka.saved-booking-producer-topic-name}")
    private String savedBookingProducerTopicName;

    @Value("${kafka.updated-booking-producer-topic-name}")
    private String updatedBookingProducerTopicName;

    @Value("${kafka.deleted-booking-producer-topic-name}")
    private String deletedBookingProducerTopicName;

    @Bean
    public NewTopic savedBookingProducer() {
        return new NewTopic(savedBookingProducerTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic updatedBookingProducer() {
        return new NewTopic(updatedBookingProducerTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic deletedBookingProducer() {
        return new NewTopic(deletedBookingProducerTopicName, 1, (short) 1);
    }

}
