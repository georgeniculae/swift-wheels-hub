package com.carrental.customer.service;

import com.carrental.dto.UserDto;
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
public class UserProducerService {

    @Value("${kafka.saved-user-producer-topic-name}")
    private String savedUserProducerTopicName;

    @Value("${kafka.updated-user-producer-topic-name}")
    private String updatedUserProducerTopicName;

    @Value("${kafka.deleted-user-producer-topic-name}")
    private String deletedUserProducerTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendSavedUser(UserDto userDto) {
        kafkaTemplate.send(buildMessage(userDto, savedUserProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent message=[" + userDto + "] with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    log.error("Unable to send message=[" + userDto + "] due to : " + e.getMessage());
                });
    }

    public void sendUpdatedUser(UserDto userDto) {
        kafkaTemplate.send(buildMessage(userDto, updatedUserProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent message=[" + userDto + "] with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    log.error("Unable to send message=[" + userDto + "] due to : " + e.getMessage());
                });
    }

    public void sendDeletedUser(String username) {
        kafkaTemplate.send(buildMessage(username, deletedUserProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent username =[" + username + "] for deleted user with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    log.error("Unable to send username=[" + username + "] for deleted user due to : " + e.getMessage());
                });
    }

    private <T> Message<T> buildMessage(T t, String topicName) {
        return MessageBuilder.withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
