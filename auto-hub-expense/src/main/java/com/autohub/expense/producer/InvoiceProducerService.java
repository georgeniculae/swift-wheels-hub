package com.autohub.expense.producer;

import com.autohub.dto.InvoiceResponse;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.emailNotificationProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "debeziumListener"
    )
    public void sendMessage(InvoiceResponse invoiceResponse) {
        try {
            kafkaTemplate.send(buildMessage(invoiceResponse, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            log.info("Sent invoice: {} with offset: {}", invoiceResponse, result.getRecordMetadata().offset());

                            return;
                        }

                        log.error("Unable to send invoice: {} due to : {}", invoiceResponse, e.getMessage());
                    })
                    .join();
        } catch (Exception e) {
            log.error("Error sending invoice: {}: {}", invoiceResponse, e.getMessage(), e);

            throw new AutoHubException("Unable to send invoice: " + invoiceResponse + " due to : " + e.getMessage());
        }
    }

    private Message<InvoiceResponse> buildMessage(InvoiceResponse invoiceResponse, String topicName) {
        return MessageBuilder.withPayload(invoiceResponse)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
