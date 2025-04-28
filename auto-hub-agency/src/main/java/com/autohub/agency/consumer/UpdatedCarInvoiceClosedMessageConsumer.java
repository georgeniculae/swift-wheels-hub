package com.autohub.agency.consumer;

import com.autohub.agency.service.CarService;
import com.autohub.dto.CarUpdateDetails;
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
public class UpdatedCarInvoiceClosedMessageConsumer {

    private final CarService carService;

    @Bean
    public Consumer<Message<CarUpdateDetails>> updatedCarInvoiceClosedConsumer() {
        return this::processCarUpdate;
    }

    private void processCarUpdate(Message<CarUpdateDetails> message) {
        carService.updateCarWhenBookingIsClosed(message.getPayload());
        KafkaUtil.acknowledgeMessage(message.getHeaders());
        log.info("Car status updated after closing booking");
    }

}
