package com.swiftwheelshub.expense.consumer;

import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.expense.service.InvoiceService;
import com.swiftwheelshub.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.GenericMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class SavedBookingMessageConsumerTest {

    @InjectMocks
    private SavedBookingMessageConsumer savedBookingMessageConsumer;

    @Mock
    private InvoiceService invoiceService;

    @Test
    void savedBookingConsumerTest_success() {
        BookingResponse bookingResponse =
                TestUtil.getResourceAsJson("/data/BookingResponse.json", BookingResponse.class);

        Message<BookingResponse> message = new GenericMessage<>(bookingResponse);

        doNothing().when(invoiceService).saveInvoice(any(BookingResponse.class));

        savedBookingMessageConsumer.savedBookingConsumer().accept(message);
    }

}
