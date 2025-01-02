package com.swiftwheelshub.lib.util;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;

import java.util.Optional;

@UtilityClass
@Slf4j
public class KafkaUtil {

    public void acknowledgeMessage(MessageHeaders messageHeaders) {
        Optional.ofNullable(messageHeaders.get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class))
                .orElseThrow(() -> new SwiftWheelsHubException("There is no Kafka acknowledgement in message headers"))
                .acknowledge();
    }

}
