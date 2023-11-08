package com.carrental.cloudgateway.config.kafka.topic;

import com.carrental.cloudgateway.config.kafka.properties.KafkaConsumerPropertiesConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.List;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaDeletedUserTopicConfig {

    private final KafkaConsumerPropertiesConfig kafkaConsumerProperties;

    @Value(value = "${spring.kafka.consumer.deleted-user-topic}")
    private String deletedUserTopic;

    @Bean
    public ReceiverOptions<String, String> deletedUserKafkaReceiverOptions() {
        ReceiverOptions<String, String> options =
                ReceiverOptions.create(kafkaConsumerProperties.kafkaConsumerProperties());

        return options.subscription(List.of(deletedUserTopic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, String> deletedUserReactiveKafkaConsumerTemplate() {
        return new ReactiveKafkaConsumerTemplate<>(deletedUserKafkaReceiverOptions());
    }

}
