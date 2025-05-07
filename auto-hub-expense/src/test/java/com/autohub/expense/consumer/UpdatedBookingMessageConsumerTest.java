package com.autohub.expense.consumer;

import com.autohub.dto.common.BookingResponse;
import com.autohub.expense.service.InvoiceService;
import com.autohub.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UpdatedBookingMessageConsumerTest {

    @InjectMocks
    private UpdatedBookingMessageConsumer updatedBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void updatedBookingConsumerTest_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Message<BookingResponse> message = MessageBuilder.withPayload(bookingResponse)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(invoiceService).updateInvoiceAfterBookingUpdate(any(BookingResponse.class));

        updatedBookingMessageConsumer.updatedBookingConsumer().accept(message);
    }

}
