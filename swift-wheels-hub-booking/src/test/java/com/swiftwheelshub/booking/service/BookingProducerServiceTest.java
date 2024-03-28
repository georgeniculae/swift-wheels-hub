package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.util.TestUtils;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingProducerServiceTest {

    @InjectMocks
    private BookingProducerService bookingProducerService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void sendSavedBookingTest_success() {
        ReflectionTestUtils.setField(bookingProducerService, "savedBookingProducerTopicName", "saved-booking-out-0");

        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();

        when(kafkaTemplate.send(any(Message.class))).thenReturn(result);

        assertDoesNotThrow(() -> bookingProducerService.sendSavedBooking(bookingResponse));
    }

    @Test
    void sendSavedBookingTest_errorOnSendingMessage() {
        ReflectionTestUtils.setField(bookingProducerService, "savedBookingProducerTopicName", "saved-booking-out-0");

        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(kafkaTemplate.send(any(Message.class))).thenThrow(new SwiftWheelsHubException("error"));

        SwiftWheelsHubException swiftWheelsHubException =
                assertThrows(SwiftWheelsHubException.class, () -> bookingProducerService.sendSavedBooking(bookingResponse));

        assertNotNull(swiftWheelsHubException);
    }

    @Test
    void sendUpdatedBookingTest_success() {
        ReflectionTestUtils.setField(bookingProducerService, "updatedBookingProducerTopicName", "updated-booking-out-0");

        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();

        when(kafkaTemplate.send(any(Message.class))).thenReturn(result);

        assertDoesNotThrow(() -> bookingProducerService.sendUpdatedBooking(bookingResponse));
    }

    @Test
    void sendUpdatedBookingTest_errorOnSendingMessage() {
        ReflectionTestUtils.setField(bookingProducerService, "updatedBookingProducerTopicName", "updated-booking-out-0");

        BookingResponse bookingResponse =
                TestUtils.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        when(kafkaTemplate.send(any(Message.class))).thenThrow(new SwiftWheelsHubException("error"));

        SwiftWheelsHubException swiftWheelsHubException =
                assertThrows(SwiftWheelsHubException.class, () -> bookingProducerService.sendSavedBooking(bookingResponse));

        assertNotNull(swiftWheelsHubException);
    }

    @Test
    void sendDeletedBookingTest_success() {
        ReflectionTestUtils.setField(bookingProducerService, "deletedBookingProducerTopicName", "deleted-booking-out-0");

        CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();

        when(kafkaTemplate.send(any(Message.class))).thenReturn(result);

        assertDoesNotThrow(() -> bookingProducerService.sendDeletedBooking(1L));
    }

    @Test
    void sendDeletedBookingTest_errorOnSendingMessage() {
        ReflectionTestUtils.setField(bookingProducerService, "deletedBookingProducerTopicName", "deleted-booking-out-0");

        when(kafkaTemplate.send(any(Message.class))).thenThrow(new SwiftWheelsHubException("error"));

        SwiftWheelsHubException swiftWheelsHubException =
                assertThrows(SwiftWheelsHubException.class, () -> bookingProducerService.sendDeletedBooking(1L));

        assertNotNull(swiftWheelsHubException);
    }

}
