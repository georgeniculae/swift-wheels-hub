package com.swiftwheelshub.booking.consumer;

import com.swiftwheelshub.booking.service.UpdatedBookingReprocessService;
import com.swiftwheelshub.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshub.lib.util.KafkaUtil;
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
