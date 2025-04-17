package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarStatusUpdate;
import com.swiftwheelshub.dto.CreatedBookingReprocessRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedBookingReprocessService {

    private final CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;
    private final BookingProducerService bookingProducerService;
    private final BookingMapper bookingMapper;

    public void reprocessCreatedBooking(CreatedBookingReprocessRequest reprocessRequest) {
        boolean isCarStatusChanged =
                createdBookingCarUpdateProducerService.changeCarStatus(getCarStatusUpdate(reprocessRequest.actualCarId()));

        if (isCarStatusChanged) {
            BookingResponse bookingResponse = bookingMapper.mapReprocessRequestToBookingResponse(reprocessRequest);
            boolean isBookingSent = bookingProducerService.sendSavedBooking(bookingResponse);

            if (isBookingSent) {
                return;
            }
        }

        throw new SwiftWheelsHubException("Failed to reprocess created booking");
    }

    private CarStatusUpdate getCarStatusUpdate(Long carId) {
        return CarStatusUpdate.builder()
                .carId(carId)
                .carState(CarState.NOT_AVAILABLE)
                .build();
    }

}
