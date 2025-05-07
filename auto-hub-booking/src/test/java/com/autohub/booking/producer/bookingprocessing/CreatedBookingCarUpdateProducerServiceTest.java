package com.autohub.booking.producer.bookingprocessing;

import com.autohub.booking.util.TestUtil;
import com.autohub.dto.common.CarStatusUpdate;
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
class CreatedBookingCarUpdateProducerServiceTest {

    @InjectMocks
    private CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void changeCarStatusTest_success() {
        ReflectionTestUtils.setField(createdBookingCarUpdateProducerService, "topicName", "saved-booking-car-update-out-0");

        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        ProducerRecord<String, Object> producerRecord =
                new ProducerRecord<>("saved-booking-out-0", 1, 2025070202L, "key", "value");

        RecordMetadata recordMetadata =
                new RecordMetadata(new TopicPartition("saved-booking-out-0", 1), 1, 1, 0, 1, 1);

        CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();
        result.complete(new SendResult<>(producerRecord, recordMetadata));

        when(kafkaTemplate.send(any(Message.class))).thenReturn(result);

        assertDoesNotThrow(() -> createdBookingCarUpdateProducerService.changeCarStatus(carStatusUpdate));
    }

    @Test
    void changeCarStatusTest_errorOnSendingMessage() {
        ReflectionTestUtils.setField(createdBookingCarUpdateProducerService, "topicName", "saved-booking-car-update-out-0");

        CarStatusUpdate carStatusUpdate =
                TestUtil.getResourceAsJson("/data/CarStatusUpdate.json", CarStatusUpdate.class);

        when(kafkaTemplate.send(any(Message.class))).thenThrow(new RuntimeException("error"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> createdBookingCarUpdateProducerService.changeCarStatus(carStatusUpdate));

        assertNotNull(autoHubException);
    }

}
