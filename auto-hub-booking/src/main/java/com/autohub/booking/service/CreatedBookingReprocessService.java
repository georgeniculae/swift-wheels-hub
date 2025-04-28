package com.autohub.booking.service;

import com.autohub.booking.mapper.BookingMapper;
import com.autohub.booking.producer.bookingprocessing.BookingProducerService;
import com.autohub.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.autohub.dto.BookingResponse;
import com.autohub.dto.CarState;
import com.autohub.dto.CarStatusUpdate;
import com.autohub.dto.CreatedBookingReprocessRequest;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatedBookingReprocessService {

    private final CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;
    private final BookingProducerService bookingProducerService;
    private final BookingMapper bookingMapper;

    public void reprocessCreatedBooking(CreatedBookingReprocessRequest reprocessRequest) {
        try {
            createdBookingCarUpdateProducerService.changeCarStatus(getCarStatusUpdate(reprocessRequest.actualCarId()));
            BookingResponse bookingResponse = bookingMapper.mapReprocessRequestToBookingResponse(reprocessRequest);
            bookingProducerService.sendSavedBooking(bookingResponse);
        } catch (Exception e) {
            throw new AutoHubException(e.getMessage());
        }
    }

    private CarStatusUpdate getCarStatusUpdate(Long carId) {
        return CarStatusUpdate.builder()
                .carId(carId)
                .carState(CarState.NOT_AVAILABLE)
                .build();
    }

}
