package com.autohub.booking.service;

import com.autohub.booking.mapper.BookingMapper;
import com.autohub.booking.producer.bookingprocessing.BookingProducerService;
import com.autohub.booking.producer.bookingprocessing.UpdateBookingUpdateCarsProducerService;
import com.autohub.dto.BookingResponse;
import com.autohub.dto.UpdateCarsRequest;
import com.autohub.dto.UpdatedBookingReprocessRequest;
import com.autohub.exception.AutoHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatedBookingReprocessService {

    private final UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;
    private final BookingProducerService bookingProducerService;
    private final BookingMapper bookingMapper;

    public void reprocessUpdatedBooking(UpdatedBookingReprocessRequest reprocessRequest) {
        try {
            updateCarsStatuses(reprocessRequest.previousCarId(), reprocessRequest.actualCarId());
            BookingResponse bookingResponse = bookingMapper.mapReprocessRequestToBookingResponse(reprocessRequest);
            bookingProducerService.sendUpdatedBooking(bookingResponse);
        } catch (Exception e) {
            throw new AutoHubException("Failed to reprocess updated booking: " + e.getMessage());
        }
    }

    private void updateCarsStatuses(Long previousCarId, Long actualCarId) {
        UpdateCarsRequest updateCarsRequest = UpdateCarsRequest.builder()
                .previousCarId(previousCarId)
                .actualCarId(actualCarId)
                .build();

        updateBookingUpdateCarsProducerService.updateCarsStatus(updateCarsRequest);
    }

}
