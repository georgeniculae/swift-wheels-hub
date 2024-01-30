package com.swiftwheelshub.audit.consumer;

import com.swiftwheelshub.audit.service.AuditLogInfoService;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class ExpenseAuditLogInfoMessageConsumer {

    private final AuditLogInfoService auditLogInfoService;

    @Bean
    public Consumer<Message<AuditLogInfoRequest>> expenseAuditInfoConsumer() {
        return auditLogInfoRequestMessage -> auditLogInfoService.saveAuditLogInfo(auditLogInfoRequestMessage.getPayload());
    }

}
