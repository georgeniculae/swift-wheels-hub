package com.swiftwheelshub.booking.consumer;

import com.swiftwheelshub.booking.service.BookingService;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class UsernameMessageConsumerTest {

    @InjectMocks
    private UsernameMessageConsumer usernameMessageConsumer;

    @Mock
    private BookingService bookingService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void usernameConsumerTest_success() {
        Message<String> message = MessageBuilder.withPayload("username")
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(bookingService).deleteBookingByCustomerUsername(anyString());

        usernameMessageConsumer.usernameConsumer().accept(message);
    }

    @Test
    void usernameConsumerTest_noAcknowledgement() {
        Message<String> message = MessageBuilder.withPayload("username")
                .build();

        doNothing().when(bookingService).deleteBookingByCustomerUsername(anyString());

        SwiftWheelsHubException swiftWheelsHubException =
                assertThrows(SwiftWheelsHubException.class, () -> usernameMessageConsumer.usernameConsumer().accept(message));

        assertEquals("There is no Kafka acknowledgement in message headers", swiftWheelsHubException.getMessage());
    }

}
