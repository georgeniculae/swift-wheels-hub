package com.swiftwheelshub.emailnotification.service;

import com.swiftwheelshub.dto.InvoiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final EmailService emailService;

    public void notifyCustomer(InvoiceDto invoiceDto) {
        emailService.sendEmail(emailService.createMail(invoiceDto.customerEmail(), invoiceDto));
    }

}
