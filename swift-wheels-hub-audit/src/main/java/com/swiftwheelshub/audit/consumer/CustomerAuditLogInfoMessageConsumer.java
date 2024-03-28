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
public class CustomerAuditLogInfoMessageConsumer {

    private final AuditLogInfoService auditLogInfoService;

    @Bean
    public Consumer<Message<AuditLogInfoRequest>> customerAuditInfoConsumer() {
        return auditLogInfoRequestMessage -> auditLogInfoService.saveCustomerAuditLogInfo(auditLogInfoRequestMessage.getPayload());
    }

}
