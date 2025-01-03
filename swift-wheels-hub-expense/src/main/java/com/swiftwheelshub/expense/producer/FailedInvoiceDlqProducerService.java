package com.swiftwheelshub.expense.producer;

import com.swiftwheelshub.dto.InvoiceReprocessRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class FailedInvoiceDlqProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.failedInvoiceDlqProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "invoiceService"
    )
    public boolean sendMessage(InvoiceReprocessRequest invoiceReprocessRequest) {
        try {
            kafkaTemplate.send(buildMessage(invoiceReprocessRequest, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            log.info(
                                    "Sent invoice reprocess request: {} with offset: {}",
                                    invoiceReprocessRequest,
                                    result.getRecordMetadata().offset()
                            );

                            return;
                        }

                        log.error("Unable to send message: {} due to : {}", invoiceReprocessRequest, e.getMessage());
                    })
                    .join();

            return true;
        } catch (Exception e) {
            throw new SwiftWheelsHubException("Error sending message: " + invoiceReprocessRequest + " " + e.getMessage());
        }
    }

    private Message<InvoiceReprocessRequest> buildMessage(InvoiceReprocessRequest invoiceReprocessRequest, String topicName) {
        return MessageBuilder.withPayload(invoiceReprocessRequest)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
