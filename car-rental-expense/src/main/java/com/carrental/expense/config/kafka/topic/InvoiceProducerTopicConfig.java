package com.carrental.expense.config.kafka.topic;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InvoiceProducerTopicConfig {

    @Value("${kafka.email-notification-producer-topic-name}")
    private String updatedInvoiceProducerTopicName;

    @Bean
    public NewTopic savedUserProducer() {
        return new NewTopic(updatedInvoiceProducerTopicName, 1, (short) 1);
    }

}
