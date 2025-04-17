package com.swiftwheelshub.booking.eventhandling;

import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.dto.BookingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletedBookingProcessorService {

    private final BookingProducerService bookingProducerService;

    public void handleBookingDeletion(BookingResponse bookingResponse) {
        bookingProducerService.sendDeletedBooking(bookingResponse.id());
    }

}
