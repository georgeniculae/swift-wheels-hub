package com.carrental.expense.config.kafka.consumer;

import com.carrental.expense.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaDeletedBookingConsumer {

    private final InvoiceService invoiceService;

    @KafkaListener(
            topics = "${kafka.deleted-booking-producer-topic-name}",
            containerFactory = "deletedBookingListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeSavedBooking(@Payload Long bookingId) {
        invoiceService.deleteInvoiceByBookingId(bookingId);
    }

}
