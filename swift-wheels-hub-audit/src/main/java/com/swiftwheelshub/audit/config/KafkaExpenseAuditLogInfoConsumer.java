package com.swiftwheelshub.audit.config;

import com.swiftwheelshub.audit.service.AuditLogInfoService;
import com.swiftwheelshub.dto.AuditLogInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaExpenseAuditLogInfoConsumer {

    private final AuditLogInfoService auditLogInfoService;

    @KafkaListener(
            topics = "${kafka.expense-audit-log-info-topic-name}",
            containerFactory = "auditListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeAuditInfo(@Payload AuditLogInfoDto expenseAuditLogInfoDto) {
        auditLogInfoService.saveAuditLogInfo(expenseAuditLogInfoDto);
    }

}
