package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.customer.mapper.CustomerMapper;
import com.swiftwheelshub.dto.CustomerInfo;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomerInfoProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CustomerMapper customerMapper;

    @Value("${spring.cloud.stream.bindings.customerInfoProducer-out-0.destination}")
    private String customerInfoProducerTopicName;

    public void sendMessage(UserRepresentation userRepresentation) {
        CustomerInfo customerInfo = customerMapper.mapToCustomerInfo(userRepresentation);

        kafkaTemplate.send(buildMessage(customerInfo, customerInfoProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent invoice=[{}] with offset=[{}]", userRepresentation, result.getRecordMetadata().offset());

                        return;
                    }

                    throw new SwiftWheelsHubException("Unable to send invoice=[" + userRepresentation + "] due to : " + e.getMessage());
                });
    }

    private Message<CustomerInfo> buildMessage(CustomerInfo customerInfo, String topicName) {
        return MessageBuilder.withPayload(customerInfo)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
