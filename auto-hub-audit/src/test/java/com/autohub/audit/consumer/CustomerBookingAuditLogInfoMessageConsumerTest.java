package com.autohub.audit.consumer;

import com.autohub.audit.service.AuditLogInfoService;
import com.autohub.audit.util.TestUtil;
import com.autohub.dto.AuditLogInfoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class CustomerBookingAuditLogInfoMessageConsumerTest {

    @InjectMocks
    private CustomerAuditLogInfoMessageConsumer customerAuditLogInfoMessageConsumer;

    @Mock
    private AuditLogInfoService auditLogInfoService;

    @Test
    void customerAuditInfoConsumerTest_success() {
        AuditLogInfoRequest auditLogInfoRequest =
                TestUtil.getResourceAsJson("/data/AuditLogInfoRequest.json", AuditLogInfoRequest.class);

        Message<AuditLogInfoRequest> message = new GenericMessage<>(auditLogInfoRequest);

        doNothing().when(auditLogInfoService).saveCustomerAuditLogInfo(any(AuditLogInfoRequest.class));

        customerAuditLogInfoMessageConsumer.customerAuditInfoConsumer().accept(message);
    }

}
