package com.swiftwheelshub.emailnotification.service;

import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.emailnotification.repository.CustomerDetailsRepository;
import com.swiftwheelshub.emailnotification.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

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

    @Mock
    private CustomerDetailsRepository customerDetailsRepository;

    @Test
    void notifyCustomerTest_success() {
        InvoiceResponse invoiceResponse =
                TestUtil.getResourceAsJson("/data/InvoiceResponse.json", InvoiceResponse.class);

        Response response = new Response();

        when(customerDetailsRepository.findByUsername(invoiceResponse.customerUsername())).thenReturn(Optional.of("user"));
        when(emailService.createMail(anyString(), any(Object.class))).thenReturn(new Mail());
        when(emailService.sendEmail(any(Mail.class))).thenReturn(response);

        assertDoesNotThrow(() -> userNotificationService.notifyCustomer(invoiceResponse));
    }

}
