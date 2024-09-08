package com.swiftwheelshub.expense.service;

import com.swiftwheelshub.dto.InvoiceResponse;
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
public class EmailNotificationProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.destination}")
    private String emailNotificationProducerTopicName;

    public void sendMessage(InvoiceResponse invoiceResponse) {
        kafkaTemplate.send(buildMessage(invoiceResponse, emailNotificationProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent invoice=[{}] with offset=[{}]", invoiceResponse, result.getRecordMetadata().offset());

                        return;
                    }

                    throw new SwiftWheelsHubException("Unable to send invoice=[" + invoiceResponse + "] due to : " + e.getMessage());
                });
    }

    private Message<InvoiceResponse> buildMessage(InvoiceResponse invoiceResponse, String topicName) {
        return MessageBuilder.withPayload(invoiceResponse)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
