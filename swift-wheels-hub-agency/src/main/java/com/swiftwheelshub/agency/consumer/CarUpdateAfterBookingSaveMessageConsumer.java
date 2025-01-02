package com.swiftwheelshub.agency.consumer;

import com.swiftwheelshub.agency.service.CarService;
import com.swiftwheelshub.dto.CarStatusUpdate;
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
public class CarUpdateAfterBookingSaveMessageConsumer {

    private final CarService carService;

    @Bean
    public Consumer<Message<CarStatusUpdate>> carUpdateAfterBookingSaveConsumer() {
        return this::processCarUpdate;
    }

    private void processCarUpdate(Message<CarStatusUpdate> message) {
        carService.updateCarStatus(message.getPayload());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Car status updated");
    }

}
