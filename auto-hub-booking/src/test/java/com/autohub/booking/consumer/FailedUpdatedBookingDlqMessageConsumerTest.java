package com.autohub.booking.consumer;

import com.autohub.booking.service.UpdatedBookingReprocessService;
import com.autohub.booking.util.TestUtil;
import com.autohub.dto.booking.UpdatedBookingReprocessRequest;
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
class FailedUpdatedBookingDlqMessageConsumerTest {

    @InjectMocks
    private FailedUpdatedBookingDlqMessageConsumer failedUpdatedBookingDlqMessageConsumer;

    @Mock
    private UpdatedBookingReprocessService updatedBookingReprocessService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void failedUpdatedBookingDlqConsumerTest_success() {
        UpdatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        Message<UpdatedBookingReprocessRequest> message = MessageBuilder.withPayload(reprocessRequest)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(updatedBookingReprocessService).reprocessUpdatedBooking(any(UpdatedBookingReprocessRequest.class));

        failedUpdatedBookingDlqMessageConsumer.failedUpdatedBookingDlqConsumer().accept(message);
    }

    @Test
    void failedUpdatedBookingDlqConsumerTest_noAcknowledgement() {
        UpdatedBookingReprocessRequest reprocessRequest =
                TestUtil.getResourceAsJson("/data/UpdatedBookingReprocessRequest.json", UpdatedBookingReprocessRequest.class);

        Message<UpdatedBookingReprocessRequest> message = MessageBuilder.withPayload(reprocessRequest)
                .build();

        doNothing().when(updatedBookingReprocessService).reprocessUpdatedBooking(any(UpdatedBookingReprocessRequest.class));

        AutoHubException autoHubException =
                assertThrows(AutoHubException.class, () -> failedUpdatedBookingDlqMessageConsumer.failedUpdatedBookingDlqConsumer().accept(message));

        assertEquals("There is no Kafka acknowledgement in message headers", autoHubException.getMessage());
    }

}
