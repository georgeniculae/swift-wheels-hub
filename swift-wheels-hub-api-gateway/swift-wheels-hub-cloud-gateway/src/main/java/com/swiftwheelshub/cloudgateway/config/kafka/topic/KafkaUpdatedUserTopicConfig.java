package com.swiftwheelshub.cloudgateway.config.kafka.topic;

import com.swiftwheelshub.cloudgateway.config.kafka.properties.KafkaConsumerPropertiesConfig;
import com.swiftwheelshub.dto.UserDto;
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
public class KafkaUpdatedUserTopicConfig {

    private final KafkaConsumerPropertiesConfig kafkaConsumerProperties;

    @Value(value = "${spring.kafka.consumer.updated-user-topic}")
    private String updatedUserTopic;

    @Bean
    public ReceiverOptions<String, UserDto> updatedUserKafkaReceiverOptions() {
        ReceiverOptions<String, UserDto> options =
                ReceiverOptions.create(kafkaConsumerProperties.kafkaConsumerProperties());

        return options.subscription(List.of(updatedUserTopic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, UserDto> updatedUserReactiveKafkaConsumerTemplate() {
        return new ReactiveKafkaConsumerTemplate<>(updatedUserKafkaReceiverOptions());
    }

}
