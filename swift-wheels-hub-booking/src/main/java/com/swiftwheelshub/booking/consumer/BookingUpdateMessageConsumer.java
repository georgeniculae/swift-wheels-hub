package com.swiftwheelshub.booking.consumer;

import com.swiftwheelshub.booking.service.BookingService;
import com.swiftwheelshub.dto.BookingClosingDetails;
import com.swiftwheelshub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BookingUpdateMessageConsumer {

    private final BookingService bookingService;

    @Bean
    public Consumer<Message<BookingClosingDetails>> bookingUpdateConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<BookingClosingDetails> message) {
        bookingService.closeBooking(message.getPayload());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
    }

}
