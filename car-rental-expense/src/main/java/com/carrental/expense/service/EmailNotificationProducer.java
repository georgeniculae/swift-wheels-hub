package com.carrental.expense.service;

import com.carrental.dto.InvoiceDto;
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

    @Value("${kafka.email-notification-producer-topic-name}")
    private String emailNotificationProducerTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(InvoiceDto invoiceDto) {
        kafkaTemplate.send(buildMessage(invoiceDto, emailNotificationProducerTopicName))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent invoice=[" + invoiceDto + "] with offset=["
                                + result.getRecordMetadata().offset() + "]");

                        return;
                    }

                    log.error("Unable to send invoice=[" + invoiceDto + "] due to : " + e.getMessage());
                });
    }

    private Message<InvoiceDto> buildMessage(InvoiceDto invoiceDto, String topicName) {
        return MessageBuilder.withPayload(invoiceDto)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
