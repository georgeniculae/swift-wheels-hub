package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.emailnotification.util.TestUtil;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserNotificationServiceTest {

    @InjectMocks
    private UserNotificationService userNotificationService;

    @Mock
    private EmailService emailService;

    @Test
    void notifyCustomerTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Response response = new Response();

        when(emailService.createMail(anyString(), any(Object.class))).thenReturn(new Mail());
        when(emailService.sendEmail(any(Mail.class))).thenReturn(response);

        assertDoesNotThrow(() -> userNotificationService.notifyCustomer(invoiceResponse));
    }

}
