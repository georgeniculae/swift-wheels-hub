package com.swiftwheelshub.emailnotification.service;

import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.emailnotification.repository.CustomerDetailsRepository;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserNotificationService {

    private final EmailService emailService;
    private final CustomerDetailsRepository customerDetailsRepository;

    public void notifyCustomer(InvoiceResponse invoiceResponse) {
        String email = customerDetailsRepository.findByUsername(invoiceResponse.customerUsername())
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Email not found"));

        Mail mail = emailService.createMail(email, invoiceResponse);
        Response response = emailService.sendEmail(mail);

        log.info("Invoice processed with status: {}{}", response.getStatusCode(), response.getBody());
    }

}
