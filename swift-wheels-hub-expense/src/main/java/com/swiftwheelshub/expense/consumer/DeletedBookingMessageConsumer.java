package com.swiftwheelshub.expense.consumer;

import com.swiftwheelshub.expense.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class DeletedBookingMessageConsumer {

    private final InvoiceService invoiceService;

    @Bean
    public Consumer<Message<Long>> deletedBookingConsumer() {
        return message -> invoiceService.deleteInvoiceByBookingId(message.getPayload());
    }

}
