package com.swiftwheelshub.booking.producer.dlq;

import com.swiftwheelshub.dto.UpdatedBookingReprocessRequest;
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
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class FailedUpdatedBookingDlqProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.failedCreatedBookingProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "debeziumListener"
    )
    public void sendFailedUpdatedBooking(UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        try {
            kafkaTemplate.send(buildMessage(updatedBookingReprocessRequest, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            logSentMessage(updatedBookingReprocessRequest, result);
                        }
                    })
                    .join();
        } catch (Exception e) {
            throw new SwiftWheelsHubException("Unable to send failed updated booking: " + updatedBookingReprocessRequest + " due to : " + e.getMessage());
        }
    }

    @Recover
    public boolean recover(Exception e, UpdatedBookingReprocessRequest updatedBookingReprocessRequest) {
        log.error("Error after re-trying to send created booking: {}: {}", updatedBookingReprocessRequest, e.getMessage(), e);

        return false;
    }

    private <T> Message<T> buildMessage(T t, String topicName) {
        return MessageBuilder.withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

    private void logSentMessage(UpdatedBookingReprocessRequest updatedBookingReprocessRequest, SendResult<String, Object> result) {
        log.info("Sent message: {} with offset: {}", updatedBookingReprocessRequest, result.getRecordMetadata().offset());
    }

}
