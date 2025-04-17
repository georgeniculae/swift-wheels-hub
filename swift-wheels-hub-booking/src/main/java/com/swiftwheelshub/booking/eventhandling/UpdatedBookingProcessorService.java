package com.swiftwheelshub.booking.eventhandling;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.UpdateBookingUpdateCarsProducerService;
import com.swiftwheelshub.booking.producer.dlq.FailedUpdatedBookingDlqProducerService;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.UpdateCarsRequest;
import com.swiftwheelshub.dto.UpdatedBookingReprocessRequest;
import com.swiftwheelshub.entity.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdatedBookingProcessorService {

    private final UpdateBookingUpdateCarsProducerService updateBookingUpdateCarsProducerService;
    private final BookingProducerService bookingProducerService;
    private final RedisTemplate<String, String> redisTemplate;
    private final FailedUpdatedBookingDlqProducerService failedUpdatedBookingDlqProducerService;
    private final BookingMapper bookingMapper;

    public void handleBookingUpdate(Booking booking, BookingResponse bookingResponse) {
        boolean areCarsUpdated = updateCarsStatuses(booking.getPreviousCarId(), booking.getActualCarId());

        if (areCarsUpdated) {
            unlockCar(booking.getPreviousCarId().toString());
            boolean isBookingUpdated = bookingProducerService.sendUpdatedBooking(bookingResponse);

            if (!isBookingUpdated) {
                UpdatedBookingReprocessRequest reprocessRequest = bookingMapper.mapToUpdatedBookingReprocessRequest(booking);
                failedUpdatedBookingDlqProducerService.sendFailedUpdatedBooking(reprocessRequest);
            }

            return;
        }

        UpdatedBookingReprocessRequest reprocessRequest = bookingMapper.mapToUpdatedBookingReprocessRequest(booking);
        failedUpdatedBookingDlqProducerService.sendFailedUpdatedBooking(reprocessRequest);
    }

    private boolean updateCarsStatuses(Long previousCarId, Long actualCarId) {
        UpdateCarsRequest updateCarsRequest = UpdateCarsRequest.builder()
                .previousCarId(previousCarId)
                .actualCarId(actualCarId)
                .build();

        return updateBookingUpdateCarsProducerService.updateCarsStatus(updateCarsRequest);
    }

    private void unlockCar(String carId) {
        redisTemplate.delete(carId);
    }

}
