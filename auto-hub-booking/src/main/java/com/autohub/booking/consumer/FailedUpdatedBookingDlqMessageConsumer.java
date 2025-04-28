package com.autohub.booking.consumer;

import com.autohub.booking.service.UpdatedBookingReprocessService;
import com.autohub.dto.UpdatedBookingReprocessRequest;
import com.autohub.lib.util.KafkaUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
public class FailedUpdatedBookingDlqMessageConsumer {

    private final UpdatedBookingReprocessService updatedBookingReprocessService;

    @Bean
    public Consumer<Message<UpdatedBookingReprocessRequest>> failedUpdatedBookingDlqConsumer() {
        return this::processMessage;
    }

    private void processMessage(Message<UpdatedBookingReprocessRequest> updatedBookingReprocessRequestMessage) {
        updatedBookingReprocessService.reprocessUpdatedBooking(updatedBookingReprocessRequestMessage.getPayload());
        KafkaUtil.acknowledgeMessage(updatedBookingReprocessRequestMessage.getHeaders());
    }

}
