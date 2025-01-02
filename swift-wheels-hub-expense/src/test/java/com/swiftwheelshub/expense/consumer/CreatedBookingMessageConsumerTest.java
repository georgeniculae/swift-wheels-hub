package com.swiftwheelshub.expense.consumer;

import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.expense.service.InvoiceService;
import com.swiftwheelshub.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class CreatedBookingMessageConsumerTest {

    @InjectMocks
    private CreatedBookingMessageConsumer createdBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void savedBookingConsumerTest_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Message<BookingResponse> message = MessageBuilder.withPayload(bookingResponse)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(invoiceService).saveInvoice(any(BookingResponse.class));

        assertDoesNotThrow(() -> createdBookingMessageConsumer.savedBookingConsumer().accept(message));
    }

    @Test
    void savedBookingConsumerTest_noAcknowledgement() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Message<BookingResponse> message = MessageBuilder.withPayload(bookingResponse)
                .build();

        doNothing().when(invoiceService).saveInvoice(any(BookingResponse.class));

        SwiftWheelsHubException swiftWheelsHubException = assertThrows(
                SwiftWheelsHubException.class,
                () -> createdBookingMessageConsumer.savedBookingConsumer().accept(message)
        );

        assertNotNull(swiftWheelsHubException.getMessage());
    }

}
