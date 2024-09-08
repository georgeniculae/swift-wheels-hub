package com.swiftwheelshub.lib.service;

import com.swiftwheelshub.dto.AuditLogInfoRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "audit", name = "enabled")
@Slf4j
public class AuditLogProducerService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.cloud.stream.bindings.auditLogInfoProducer-out-0.destination}")
    private String topic;

    public void sendAuditLog(AuditLogInfoRequest auditLogInfo) {
        kafkaTemplate.send(buildMessage(auditLogInfo, topic))
                .whenComplete((result, e) -> {
                    if (ObjectUtils.isEmpty(e)) {
                        log.info("Sent message=[{}] with offset=[{}]", auditLogInfo, result.getRecordMetadata().offset());

                        return;
                    }

                    log.error("Unable to send message=[{}] due to :{} ", auditLogInfo, e.getMessage());
                });
    }

    private Message<AuditLogInfoRequest> buildMessage(AuditLogInfoRequest auditLogInfoRequest, String topicName) {
        return MessageBuilder.withPayload(auditLogInfoRequest)
                .setHeader(KafkaHeaders.TOPIC, topicName)
                .build();
    }

}
