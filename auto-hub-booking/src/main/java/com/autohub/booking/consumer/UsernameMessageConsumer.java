package com.autohub.booking.consumer;

import com.autohub.booking.service.BookingService;
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
public class UsernameMessageConsumer {

    private final BookingService bookingService;

    @Bean
    public Consumer<Message<String>> usernameConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<String> message) {
        bookingService.deleteBookingByCustomerUsername(message.getPayload());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
    }

}
