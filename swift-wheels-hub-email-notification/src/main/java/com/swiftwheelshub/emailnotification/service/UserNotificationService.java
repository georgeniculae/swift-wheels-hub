package com.swiftwheelshub.emailnotification.service;

import com.swiftwheelshub.dto.InvoiceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final EmailService emailService;

    public void notifyCustomer(InvoiceResponse invoiceResponse) {
        emailService.sendEmail(emailService.createMail(invoiceResponse.customerEmail(), invoiceResponse));
    }

}
