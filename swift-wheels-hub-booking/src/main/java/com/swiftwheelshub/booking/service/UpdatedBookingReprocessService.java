package com.swiftwheelshub.booking.service;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.UpdateBookingUpdateCarsProducerService;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.UpdateCarsRequest;
import com.swiftwheelshub.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatedBookingReprocessService {

    private final UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;
    private final BookingProducerService bookingProducerService;
    private final BookingMapper bookingMapper;

    public void reprocessUpdatedBooking(UpdatedBookingReprocessRequest reprocessRequest) {
        boolean areCarsUpdated = updateCarsStatuses(reprocessRequest.previousCarId(), reprocessRequest.actualCarId());

        if (areCarsUpdated) {
            BookingResponse bookingResponse = bookingMapper.mapReprocessRequestToBookingResponse(reprocessRequest);
            boolean isBookingUpdated = bookingProducerService.sendUpdatedBooking(bookingResponse);

            if (isBookingUpdated) {
                return;
            }
        }

        throw new SwiftWheelsHubException("Failed to reprocess updated booking");
    }

    private boolean updateCarsStatuses(Long previousCarId, Long actualCarId) {
        UpdateCarsRequest updateCarsRequest = UpdateCarsRequest.builder()
                .previousCarId(previousCarId)
                .actualCarId(actualCarId)
                .build();

        return updateBookingUpdateCarsProducerService.updateCarsStatus(updateCarsRequest);
    }

}
