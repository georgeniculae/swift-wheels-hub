package com.swiftwheelshub.expense.consumer;

import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.expense.service.InvoiceService;
import com.swiftwheelshub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class CreatedBookingMessageConsumer {

    private final InvoiceService invoiceService;

    @Bean
    public Consumer<Message<BookingResponse>> savedBookingConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<BookingResponse> bookingResponseMessage) {
        invoiceService.saveInvoice(bookingResponseMessage.getPayload());
        KafkaUtil.acknowledgeMessage(bookingResponseMessage.getHeaders());
    }

}
