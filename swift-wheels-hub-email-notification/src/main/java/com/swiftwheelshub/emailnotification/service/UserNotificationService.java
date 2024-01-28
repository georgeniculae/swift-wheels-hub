package com.swiftwheelshub.emailnotification.service;

import com.sendgrid.helpers.mail.Mail;
import com.swiftwheelshub.dto.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final EmailService emailService;

    public void notifyCustomer(InvoiceResponse invoiceResponse) {
        Mail mail = emailService.createMail(invoiceResponse.customerEmail(), invoiceResponse);
        emailService.sendEmail(mail);
    }

}
