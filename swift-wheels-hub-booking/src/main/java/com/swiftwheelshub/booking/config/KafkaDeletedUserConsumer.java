package com.swiftwheelshub.booking.config;

import com.swiftwheelshub.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaDeletedUserConsumer {

    private final BookingService bookingService;

    @KafkaListener(
            topics = "${kafka.deleted-user-topic-name}",
            containerFactory = "deletedUserListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeSavedBooking(@Payload String username) {
        bookingService.deleteBookingsByUsername(username);
    }

}
