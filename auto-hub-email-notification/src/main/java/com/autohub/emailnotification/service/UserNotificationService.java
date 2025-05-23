package com.autohub.emailnotification.service;

import com.autohub.dto.common.InvoiceResponse;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationService {

    private final EmailService emailService;

    public void notifyCustomer(InvoiceResponse invoiceResponse) {
        Mail mail = emailService.createMail(invoiceResponse.customerEmail(), invoiceResponse);
        Response response = emailService.sendEmail(mail);

        log.info("Invoice processed with status: {}{}", response.getStatusCode(), response.getBody());
    }

}
