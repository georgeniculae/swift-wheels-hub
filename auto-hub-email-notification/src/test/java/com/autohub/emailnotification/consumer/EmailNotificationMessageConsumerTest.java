package com.autohub.emailnotification.consumer;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.service.UserNotificationService;
import com.autohub.emailnotification.util.TestUtil;
import com.autohub.exception.AutoHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class EmailNotificationMessageConsumerTest {

    @InjectMocks
    private EmailNotificationMessageConsumer emailNotificationMessageConsumer;

    @Mock
    private UserNotificationService userNotificationService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void emailNotificationConsumerTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Message<InvoiceResponse> message = MessageBuilder.withPayload(invoiceResponse)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(userNotificationService).notifyCustomer(any(InvoiceResponse.class));

        assertDoesNotThrow(() -> emailNotificationMessageConsumer.emailNotificationConsumer().accept(message));
    }

    @Test
    void emailNotificationConsumerTest_noAcknowledgment() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Message<InvoiceResponse> message = MessageBuilder.withPayload(invoiceResponse)
                .build();

        doNothing().when(userNotificationService).notifyCustomer(any(InvoiceResponse.class));

        AutoHubException autoHubException = assertThrows(
                AutoHubException.class,
                () -> emailNotificationMessageConsumer.emailNotificationConsumer().accept(message)
        );

        assertNotNull(autoHubException);
    }

}
