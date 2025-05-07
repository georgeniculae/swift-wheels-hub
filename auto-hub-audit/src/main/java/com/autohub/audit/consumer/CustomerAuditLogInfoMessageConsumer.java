package com.autohub.audit.consumer;

import com.autohub.audit.service.AuditLogInfoService;
import com.autohub.dto.common.AuditLogInfoRequest;
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
