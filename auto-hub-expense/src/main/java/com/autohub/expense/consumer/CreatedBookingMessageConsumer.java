package com.autohub.expense.consumer;

import com.autohub.dto.common.BookingResponse;
import com.autohub.expense.service.InvoiceService;
import com.autohub.lib.util.KafkaUtil;
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
