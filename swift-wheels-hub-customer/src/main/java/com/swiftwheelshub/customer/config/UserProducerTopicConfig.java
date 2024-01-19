package com.swiftwheelshub.customer.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserProducerTopicConfig {

    @Value("${kafka.saved-user-producer-topic-name}")
    private String savedUserProducerTopicName;

    @Value("${kafka.updated-user-producer-topic-name}")
    private String updatedUserProducerTopicName;

    @Value("${kafka.deleted-user-producer-topic-name}")
    private String deletedUserProducerTopicName;

    @Bean
    public NewTopic savedUserProducer() {
        return new NewTopic(savedUserProducerTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic updatedUserProducer() {
        return new NewTopic(updatedUserProducerTopicName, 1, (short) 1);
    }

    @Bean
    public NewTopic deleteUserProducer() {
        return new NewTopic(updatedUserProducerTopicName, 1, (short) 1);
    }

}
