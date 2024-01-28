package com.swiftwheelshub.emailnotification.service;

import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private SendGrid sendGrid;

    @Test
    void sendEmailTest_success() throws IOException {
        Mail mail = new Mail();
        Response response = new Response();

        when(sendGrid.api(any(Request.class))).thenReturn(response);

        assertDoesNotThrow(() -> emailService.sendEmail(mail));
    }

}
