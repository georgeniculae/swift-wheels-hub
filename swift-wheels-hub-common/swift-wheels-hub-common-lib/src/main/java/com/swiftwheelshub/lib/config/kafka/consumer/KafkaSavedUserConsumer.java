package com.swiftwheelshub.lib.config.kafka.consumer;

import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.lib.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "kafka", name = "user-consumer")
public class KafkaSavedUserConsumer {

    private final UserService userService;

    @KafkaListener(
            topics = "${kafka.saved-user-producer-topic-name}",
            containerFactory = "userListenerContainerFactory",
            groupId = "${kafka.groupId}"
    )
    public void consumeSavedUser(@Payload UserDto userDto) {
        userService.saveUser(userDto);
    }

}
