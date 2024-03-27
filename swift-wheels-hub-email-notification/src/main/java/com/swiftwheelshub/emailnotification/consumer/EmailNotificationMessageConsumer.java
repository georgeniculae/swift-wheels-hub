package com.swiftwheelshub.emailnotification.consumer;

import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.emailnotification.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class EmailNotificationMessageConsumer {

    private final UserNotificationService userNotificationService;

    @Bean
    public Consumer<Message<InvoiceResponse>> invoiceConsumer() {
        return invoiceResponseMessage -> userNotificationService.notifyCustomer(invoiceResponseMessage.getPayload());
    }

}
