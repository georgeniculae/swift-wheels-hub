package com.autohub.booking.producer.dlq;

import com.autohub.dto.booking.UpdatedBookingReprocessRequest;
import com.autohub.exception.AutoHubException;
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
public class FailedUpdatedBookingDlqProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.failedUpdatedBookingDlqProducer-out-0.destination}")
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
            throw new AutoHubException("Unable to send failed updated booking: " + updatedBookingReprocessRequest + " due to : " + e.getMessage());
        }
    }

    private Message<UpdatedBookingReprocessRequest> buildMessage(UpdatedBookingReprocessRequest updatedBookingReprocessRequest, String topicName) {
        return MessageBuilder.withPayload(updatedBookingReprocessRequest)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

    private void logSentMessage(UpdatedBookingReprocessRequest updatedBookingReprocessRequest, SendResult<String, Object> result) {
        log.info("Sent message: {} with offset: {}", updatedBookingReprocessRequest, result.getRecordMetadata().offset());
    }

}
