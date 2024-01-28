package com.swiftwheelshub.audit.config;

import com.swiftwheelshub.audit.service.AuditLogInfoService;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaBookingAuditLogInfoConsumer {

    private final AuditLogInfoService auditLogInfoService;

    @KafkaListener(
            topics = "${kafka.booking-audit-log-info-topic-name}",
            containerFactory = "auditListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeAuditInfo(@Payload AuditLogInfoRequest bookingAuditLogInfoRequest) {
        auditLogInfoService.saveAuditLogInfo(bookingAuditLogInfoRequest);
    }

}
