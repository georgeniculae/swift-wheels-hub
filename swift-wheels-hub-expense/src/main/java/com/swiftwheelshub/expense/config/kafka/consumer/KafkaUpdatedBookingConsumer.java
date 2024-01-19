package com.swiftwheelshub.expense.config.kafka.consumer;

import com.swiftwheelshub.dto.BookingDto;
import com.swiftwheelshub.expense.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaUpdatedBookingConsumer {

    private final InvoiceService invoiceService;

    @KafkaListener(
            topics = "${kafka.updated-booking-producer-topic-name}",
            containerFactory = "bookingListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeUpdatedBooking(@Payload BookingDto bookingDto) {
        invoiceService.updateInvoiceAfterBookingUpdate(bookingDto);
    }

}
