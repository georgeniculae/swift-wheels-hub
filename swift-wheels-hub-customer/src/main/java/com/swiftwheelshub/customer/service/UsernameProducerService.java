package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
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
public class UsernameProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.savedBookingProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "customerService"
    )
    public void sendUsername(String username) {
        try {
            kafkaTemplate.send(buildMessage(username, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            logSentMessage(username, result);
                        }
                    })
                    .join();
        } catch (Exception e) {
            throw new SwiftWheelsHubException("Unable to send username: " + username + " due to : " + e.getMessage());
        }
    }

    private Message<String> buildMessage(String username, String topicName) {
        return MessageBuilder.withPayload(username)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

    private void logSentMessage(String username, SendResult<String, Object> result) {
        log.info("Sent message: {} with offset: {}", username, result.getRecordMetadata().offset());
    }

}
