package com.swiftwheelshub.audit.consumer;

import com.swiftwheelshub.audit.service.AuditLogInfoService;
import com.swiftwheelshub.dto.AuditLogInfoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class ExpenseAuditLogInfoMessageConsumer {

    private final AuditLogInfoService auditLogInfoService;

    @Bean
    public Consumer<Message<AuditLogInfoRequest>> expenseAuditInfoConsumer() {
        return auditLogInfoRequestMessage -> auditLogInfoService.saveExpenseAuditLogInfo(auditLogInfoRequestMessage.getPayload());
    }

}
