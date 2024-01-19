package com.swiftwheelshub.expense.config.kafka.consumer;

import com.swiftwheelshub.dto.BookingDto;
import com.swiftwheelshub.expense.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaSavedBookingConsumer {

    private final InvoiceService invoiceService;

    @KafkaListener(
            topics = "${kafka.saved-booking-producer-topic-name}",
            containerFactory = "bookingListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeSavedBooking(@Payload BookingDto bookingDto) {
        invoiceService.saveInvoice(bookingDto);
    }

}
