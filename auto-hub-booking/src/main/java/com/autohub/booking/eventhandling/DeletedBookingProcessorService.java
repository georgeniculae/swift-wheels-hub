package com.autohub.booking.eventhandling;

import com.autohub.booking.producer.bookingprocessing.BookingProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletedBookingProcessorService {

    private final BookingProducerService bookingProducerService;

    public void handleBookingDeletion(Long bookingId) {
        bookingProducerService.sendDeletedBooking(bookingId);
    }

}
