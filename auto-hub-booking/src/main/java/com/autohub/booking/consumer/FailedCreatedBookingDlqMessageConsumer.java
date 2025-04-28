package com.autohub.booking.consumer;

import com.autohub.booking.service.CreatedBookingReprocessService;
import com.autohub.dto.CreatedBookingReprocessRequest;
import com.autohub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class FailedCreatedBookingDlqMessageConsumer {

    private final CreatedBookingReprocessService createdBookingReprocessService;

    @Bean
    public Consumer<Message<CreatedBookingReprocessRequest>> failedCreatedBookingDlqConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<CreatedBookingReprocessRequest> createdBookingReprocessRequestMessage) {
        createdBookingReprocessService.reprocessCreatedBooking(createdBookingReprocessRequestMessage.getPayload());
        KafkaUtil.acknowledgeMessage(createdBookingReprocessRequestMessage.getHeaders());
    }

}
