package com.swiftwheelshub.agency.consumer;

import com.swiftwheelshub.agency.service.CarService;
import com.swiftwheelshub.dto.UpdateCarsRequest;
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
public class UpdatedCarBookingUpdatedMessageConsumer {

    private final CarService carService;

    @Bean
    public Consumer<Message<UpdateCarsRequest>> updatedCarBookingUpdatedConsumer() {
        return this::processCarUpdate;
    }

    private void processCarUpdate(Message<UpdateCarsRequest> message) {
        carService.updateCarsStatus(message.getPayload());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Car status updated after booking update");
    }

}
