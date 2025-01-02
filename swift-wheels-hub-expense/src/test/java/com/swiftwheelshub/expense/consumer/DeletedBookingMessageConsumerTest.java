package com.swiftwheelshub.expense.consumer;

import com.swiftwheelshub.expense.service.InvoiceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class DeletedBookingMessageConsumerTest {

    @InjectMocks
    private DeletedBookingMessageConsumer deletedBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Mock
    private Acknowledgment acknowledgment;

    @Test
    void deletedBookingConsumerTest_success() {
        Message<Long> message = MessageBuilder.withPayload(1L)
                .setHeader(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment)
                .build();

        doNothing().when(invoiceService).deleteInvoiceByBookingId(anyLong());

        deletedBookingMessageConsumer.deletedBookingConsumer().accept(message);
    }

}
