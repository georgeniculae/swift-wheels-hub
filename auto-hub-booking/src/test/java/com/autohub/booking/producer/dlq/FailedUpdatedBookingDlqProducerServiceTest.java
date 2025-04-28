package com.autohub.booking.producer.dlq;

import com.autohub.booking.util.TestUtil;
import com.autohub.dto.UpdatedBookingReprocessRequest;
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
class FailedUpdatedBookingDlqProducerServiceTest {

    @InjectMocks
    private FailedUpdatedBookingDlqProducerService failedUpdatedBookingDlqProducerService;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void sendFailedUpdatedBookingTest_success() {
        ReflectionTestUtils.setField(failedUpdatedBookingDlqProducerService, "topicName", "saved-booking-out-0");

        UpdatedBookingReprocessRequest updatedBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        ProducerRecord<String, Object> producerRecord =
                new ProducerRecord<>("failed-updated-booking-out-0", 1, 2025070202L, "key", "value");

        RecordMetadata recordMetadata =
                new RecordMetadata(new TopicPartition("failed-updated-booking-out-0", 1), 1, 1, 0, 1, 1);

        CompletableFuture<SendResult<String, Object>> result = new CompletableFuture<>();
        result.complete(new SendResult<>(producerRecord, recordMetadata));

        when(kafkaTemplate.send(any(Message.class))).thenReturn(result);

        assertDoesNotThrow(() -> failedUpdatedBookingDlqProducerService.sendFailedUpdatedBooking(updatedBookingReprocessRequest));
    }

    @Test
    void sendFailedCreatedBookingTest_errorOnSendingMessage() {
        ReflectionTestUtils.setField(failedUpdatedBookingDlqProducerService, "topicName", "saved-booking-out-0");

        UpdatedBookingReprocessRequest updatedBookingReprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        when(kafkaTemplate.send(any(Message.class))).thenThrow(new RuntimeException("error"));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> failedUpdatedBookingDlqProducerService.sendFailedUpdatedBooking(updatedBookingReprocessRequest));

        assertNotNull(autoHubException);
    }

}
