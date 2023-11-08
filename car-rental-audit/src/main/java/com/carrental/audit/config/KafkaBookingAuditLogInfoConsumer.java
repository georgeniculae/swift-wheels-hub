package com.carrental.audit.config;

import com.carrental.audit.service.AuditLogInfoService;
import com.carrental.dto.AuditLogInfoDto;
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
    public void consumeAuditInfo(@Payload AuditLogInfoDto bookingAuditLogInfoDto) {
        auditLogInfoService.saveAuditLogInfo(bookingAuditLogInfoDto);
    }

}
