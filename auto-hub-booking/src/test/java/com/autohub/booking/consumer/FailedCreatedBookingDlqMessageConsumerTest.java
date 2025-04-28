package com.autohub.booking.consumer;

import com.autohub.booking.service.CreatedBookingReprocessService;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.CreatedBookingReprocessRequest;
import com.autohub.exception.AutoHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class FailedCreatedBookingDlqMessageConsumerTest {

    @InjectMocks
    private FailedCreatedBookingDlqMessageConsumer failedCreatedBookingDlqMessageConsumer;

    @Mock
    private CreatedBookingReprocessService createdBookingReprocessService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void failedCreatedBookingDlqConsumerTest_success() {
        CreatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        Message<CreatedBookingReprocessRequest> message = MessageBuilder.withPayload(reprocessRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(createdBookingReprocessService).reprocessCreatedBooking(any(CreatedBookingReprocessRequest.class));

        failedCreatedBookingDlqMessageConsumer.failedCreatedBookingDlqConsumer().accept(message);
    }

    @Test
    void failedCreatedBookingDlqConsumerTest_noAcknowledgement() {
        CreatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/CreatedBookingReprocessRequest.json", CreatedBookingReprocessRequest.class);

        Message<CreatedBookingReprocessRequest> message = MessageBuilder.withPayload(reprocessRequest)
                .build();

        doNothing().when(createdBookingReprocessService).reprocessCreatedBooking(any(CreatedBookingReprocessRequest.class));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> failedCreatedBookingDlqMessageConsumer.failedCreatedBookingDlqConsumer().accept(message));

        assertEquals("There is no Kafka acknowledgement in message headers", autoHubException.getMessage());
    }

}
