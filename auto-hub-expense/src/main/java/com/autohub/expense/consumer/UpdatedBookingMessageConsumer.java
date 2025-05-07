package com.autohub.expense.consumer;

import com.autohub.dto.common.BookingResponse;
import com.autohub.expense.service.InvoiceService;
import com.autohub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class UpdatedBookingMessageConsumer {

    private final InvoiceService invoiceService;

    @Bean
    public Consumer<Message<BookingResponse>> updatedBookingConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<BookingResponse> bookingResponseMessage) {
        invoiceService.updateInvoiceAfterBookingUpdate(bookingResponseMessage.getPayload());
        KafkaUtil.acknowledgeMessage(bookingResponseMessage.getHeaders());
        log.info("Invoice updated after booking update");
    }

}
