package com.swiftwheelshub.booking.scheduler;

import com.swiftwheelshub.booking.repository.BookingRepository;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.BookingProcessStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class FailedBookingScheduler {

    private final BookingRepository bookingRepository;

    @Scheduled(fixedDelay = 5000)
    @Transactional(readOnly = true)
    public void processFailedBookings() {
        try (Stream<Booking> failedBookings = bookingRepository.findAllFailedBookings()) {
            failedBookings.forEach(failedBooking -> {
                BookingProcessStatus bookingProcessStatus = getBookingProcessStatus(failedBooking.getBookingProcessStatus());
                failedBooking.setBookingProcessStatus(bookingProcessStatus);
                bookingRepository.save(failedBooking);
            });
        }
    }

    private BookingProcessStatus getBookingProcessStatus(BookingProcessStatus bookingProcessStatus) {
        return switch (bookingProcessStatus) {
            case FAILED_CREATED_BOOKING -> BookingProcessStatus.SAVED_CREATED_BOOKING;
            case FAILED_UPDATED_BOOKING -> BookingProcessStatus.SAVED_UPDATED_BOOKING;
            default -> BookingProcessStatus.SAVED_CLOSED_BOOKING;
        };
    }

}
