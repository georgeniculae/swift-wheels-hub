package com.swiftwheelshub.emailnotification.consumer;

import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.emailnotification.service.UserNotificationService;
import com.swiftwheelshub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationMessageConsumer {

    private final UserNotificationService userNotificationService;

    @Bean
    public Consumer<Message<InvoiceResponse>> emailNotificationConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<InvoiceResponse> message) {
        userNotificationService.notifyCustomer(message.getPayload());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Email notification sent");
    }

}
