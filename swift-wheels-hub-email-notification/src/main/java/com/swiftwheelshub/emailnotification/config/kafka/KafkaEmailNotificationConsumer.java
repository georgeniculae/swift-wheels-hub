package com.swiftwheelshub.emailnotification.config.kafka;

import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.emailnotification.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaEmailNotificationConsumer {

    private final UserNotificationService userNotificationService;

    @KafkaListener(
            topics = "${kafka.email-notification-producer-topic-name}",
            containerFactory = "emailNotificationListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeInvoice(@Payload InvoiceDto invoiceDto) {
        userNotificationService.notifyCustomer(invoiceDto);
    }

}
