package com.swiftwheelshub.booking.producer;

import com.swiftwheelshub.dto.UpdateCarsRequest;
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
public class UpdateBookingUpdateCarsProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.updateBookingCarsUpdateProducer-out-0.destination}")
    private String topicName;

    @Retryable(
            retryFor = Exception.class,
            maxAttempts = 5,
            backoff = @Backoff(value = 5000L),
            listeners = "bookingService"
    )
    public boolean updateCarsStatus(UpdateCarsRequest updateCarsRequest) {
        try {
            kafkaTemplate.send(buildMessage(updateCarsRequest, topicName))
                    .whenComplete((result, e) -> {
                        if (ObjectUtils.isEmpty(e)) {
                            logSentMessage(updateCarsRequest, result);

                            return;
                        }

                        log.error("Unable to send message: {} due to : {}", updateCarsRequest, e.getMessage());
                    })
                    .join();

            return true;
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage(), e);

            return false;
        }
    }

    @Recover
    public boolean recover(Exception e, UpdateCarsRequest updateCarsRequest) {
        log.error("Error after re-trying update cars status: {}: {}", updateCarsRequest, e.getMessage(), e);

        return false;
    }

    private <T> Message<T> buildMessage(T t, String topicName) {
        return MessageBuilder.withPayload(t)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

    private void logSentMessage(UpdateCarsRequest updateCarsRequest, SendResult<String, Object> result) {
        log.info("Sent message: {} with offset: {}", updateCarsRequest, result.getRecordMetadata().offset());
    }

}
