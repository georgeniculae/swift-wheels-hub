package com.autohub.booking.consumer;

import com.autohub.booking.service.BookingService;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.common.BookingClosingDetails;
import com.autohub.exception.AutoHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class BookingUpdateMessageConsumerTest {

    @InjectMocks
    private BookingUpdateMessageConsumer bookingUpdateMessageConsumer;

    @Mock
    private BookingService bookingService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void bookingUpdateConsumerTest_success() {
        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        Message<BookingClosingDetails> message = MessageBuilder.withPayload(bookingClosingDetails)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(bookingService).closeBooking(any(BookingClosingDetails.class));

        bookingUpdateMessageConsumer.bookingUpdateConsumer().accept(message);
    }

    @Test
    void bookingUpdateConsumerTest_noAcknowledgement() {
        BookingClosingDetails bookingClosingDetails =
                TestUtil.getResourceAsJson("/data/BookingClosingDetails.json", BookingClosingDetails.class);

        Message<BookingClosingDetails> message = MessageBuilder.withPayload(bookingClosingDetails)
                .build();

        doNothing().when(bookingService).closeBooking(any(BookingClosingDetails.class));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> bookingUpdateMessageConsumer.bookingUpdateConsumer().accept(message));

        assertEquals("There is no Kafka acknowledgement in message headers", autoHubException.getMessage());
    }

}
