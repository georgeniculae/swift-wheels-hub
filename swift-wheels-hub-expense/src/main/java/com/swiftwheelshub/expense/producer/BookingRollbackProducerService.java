package com.swiftwheelshub.expense.producer;

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
public class BookingRollbackProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.bookingRollbackProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = {"invoiceService", "invoiceReprocessingService"}
    )
    public boolean rollbackBooking(Long bookingId) {
        try {
            kafkaTemplate.send(buildMessage(bookingId, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            log.info("Sent booking id: {} with offset: {}", bookingId, result.getRecordMetadata().offset());

                            return;
                        }

                        log.error("Unable to send message: {} due to : {}", bookingId, e.getMessage());
                    })
                    .join();

            return true;
        } catch (Exception e) {
            log.error("Error sending message: {}: {}", bookingId, e.getMessage(), e);

            return false;
        }
    }

    private Message<Long> buildMessage(Long bookingId, String topicName) {
        return MessageBuilder.withPayload(bookingId)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
