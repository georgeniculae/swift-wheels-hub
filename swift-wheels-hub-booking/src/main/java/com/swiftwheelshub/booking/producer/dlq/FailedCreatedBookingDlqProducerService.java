package com.swiftwheelshub.booking.producer.dlq;

import com.swiftwheelshub.dto.CreatedBookingReprocessRequest;
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
public class FailedCreatedBookingDlqProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.failedCreatedBookingDlqProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "debeziumListener"
    )
    public void sendFailedCreatedBooking(CreatedBookingReprocessRequest createdBookingReprocessRequest) {
        try {
            kafkaTemplate.send(buildMessage(createdBookingReprocessRequest, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            logSentMessage(createdBookingReprocessRequest, result);
                        }
                    })
                    .join();
        } catch (Exception e) {
            throw new SwiftWheelsHubException("Unable to send failed created booking: " + createdBookingReprocessRequest + " due to : " + e.getMessage());
        }
    }

    private <T> Message<T> buildMessage(T t, String topicName) {
        return MessageBuilder.withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

    private void logSentMessage(CreatedBookingReprocessRequest createdBookingReprocessRequest, SendResult<String, Object> result) {
        log.info("Sent message: {} with offset: {}", createdBookingReprocessRequest, result.getRecordMetadata().offset());
    }

}
