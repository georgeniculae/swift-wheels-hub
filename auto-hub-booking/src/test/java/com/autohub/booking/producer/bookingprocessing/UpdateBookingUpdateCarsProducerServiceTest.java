package com.autohub.booking.producer.bookingprocessing;

import com.autohub.booking.util.TestUtil;
import com.autohub.dto.UpdateCarsRequest;
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
class UpdateBookingUpdateCarsProducerServiceTest {

    @InjectMocks
    private UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void updateCarsStatusTest_success() {
        ReflectionTestUtils.setField(updateBookingUpdateCarsProducerService, "topicName", "updated-booking-cars-update-out-0");

        UpdateCarsRequest updateCarsRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarsRequest.json", UpdateCarsRequest.class);

        ProducerRecord<String, Object> producerRecord =
                new ProducerRecord<>("saved-booking-out-0", 1, 2025070202L, "key", "value");

        RecordMetadata recordMetadata =
                new RecordMetadata(new TopicPartition("saved-booking-out-0", 1), 1, 1, 0, 1, 1);

        CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();
        result.complete(new SendResult<>(producerRecord, recordMetadata));

        when(kafkaTemplate.send(any(Message.class))).thenReturn(result);

        assertDoesNotThrow(() -> updateBookingUpdateCarsProducerService.updateCarsStatus(updateCarsRequest));
    }

    @Test
    void updateCarsStatusTest_errorOnSendingMessage() {
        ReflectionTestUtils.setField(updateBookingUpdateCarsProducerService, "topicName", "updated-booking-cars-update-out-0");

        UpdateCarsRequest updateCarsRequest =
                TestUtil.getResourceAsJson("/data/UpdateCarsRequest.json", UpdateCarsRequest.class);

        when(kafkaTemplate.send(any(Message.class))).thenThrow(new RuntimeException("error"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> updateBookingUpdateCarsProducerService.updateCarsStatus(updateCarsRequest));

        assertNotNull(autoHubException);
    }

}
