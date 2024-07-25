package com.swiftwheelshub.emailnotification.consumer;

import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.emailnotification.service.UserNotificationService;
import com.swiftwheelshub.emailnotification.util.TestUtil;
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
class EmailNotificationMessageConsumerTest {

    @InjectMocks
    private EmailNotificationMessageConsumer emailNotificationMessageConsumer;

    @Mock
    private UserNotificationService userNotificationService;

    @Test
    void emailNotificationConsumerTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Message<InvoiceResponse> message = new GenericMessage<>(invoiceResponse);

        doNothing().when(userNotificationService).notifyCustomer(any(InvoiceResponse.class));

        emailNotificationMessageConsumer.emailNotificationConsumer().accept(message);
    }

}
