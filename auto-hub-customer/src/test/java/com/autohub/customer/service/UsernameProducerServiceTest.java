package com.autohub.customer.service;

import com.autohub.exception.AutoHubException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.messaging.Message;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsernameProducerServiceTest {

    @InjectMocks
    private UsernameProducerService usernameProducerService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void sendSavedBookingTest_success() {
        ReflectionTestUtils.setField(usernameProducerService, "topicName", "username-out-0");

        ProducerRecord<String, Object> producerRecord =
                new ProducerRecord<>("username-out-0", 1, 2025070202L, "key", "value");

        RecordMetadata recordMetadata =
                new RecordMetadata(new TopicPartition("username-out-0", 1), 1, 1, 0, 1, 1);

        CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();
        result.complete(new SendResult<>(producerRecord, recordMetadata));

        when(kafkaTemplate.send(any(Message.class))).thenReturn(result);

        assertDoesNotThrow(() -> usernameProducerService.sendUsername("username"));
    }

    @Test
    void sendSavedBookingTest_errorOnSendingMessage() {
        ReflectionTestUtils.setField(usernameProducerService, "topicName", "username-out-0");

        when(kafkaTemplate.send(any(Message.class))).thenThrow(new RuntimeException("error"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> usernameProducerService.sendUsername("username"));

        assertNotNull(autoHubException);
    }

}
