package com.swiftwheelshub.emailnotification.consumer;

import com.swiftwheelshub.dto.CustomerInfo;
import com.swiftwheelshub.emailnotification.service.CustomerDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ConsumerDetailsConsumer {

    private final CustomerDetailsService customerDetailsService;

    @Bean
    public Consumer<Message<CustomerInfo>> consumerInfoConsumer() {
        return message -> customerDetailsService.saveCustomerDetails(message.getPayload());
    }

}
