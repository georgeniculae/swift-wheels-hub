package com.swiftwheelshub.booking.eventhandling;

import com.swiftwheelshub.booking.mapper.BookingMapper;
import com.swiftwheelshub.booking.producer.bookingprocessing.BookingProducerService;
import com.swiftwheelshub.booking.producer.bookingprocessing.CreatedBookingCarUpdateProducerService;
import com.swiftwheelshub.booking.producer.dlq.FailedCreatedBookingDlqProducerService;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarStatusUpdate;
import com.swiftwheelshub.dto.CreatedBookingReprocessRequest;
import com.swiftwheelshub.entity.Booking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatedBookingProcessorService {

    private final CreatedBookingCarUpdateProducerService createdBookingCarUpdateProducerService;
    private final BookingProducerService bookingProducerService;
    private final RedisTemplate<String, String> redisTemplate;
    private final FailedCreatedBookingDlqProducerService failedCreatedBookingDlqProducerService;
    private final BookingMapper bookingMapper;

    public void handleBookingCreation(Booking booking) {
        try {
            createdBookingCarUpdateProducerService.changeCarStatus(getCarStatusUpdate(booking.getActualCarId()));
            unlockCar(booking.getActualCarId().toString());

            BookingResponse bookingResponse = bookingMapper.mapEntityToDto(booking);
            bookingProducerService.sendSavedBooking(bookingResponse);
        } catch (Exception e) {
            log.error("Error while creating booking: {}", e.getMessage());

            CreatedBookingReprocessRequest reprocessRequest = bookingMapper.mapToCreatedBookingReprocessRequest(booking);
            failedCreatedBookingDlqProducerService.sendFailedCreatedBooking(reprocessRequest);
        }
    }

    private CarStatusUpdate getCarStatusUpdate(Long carId) {
        return CarStatusUpdate.builder()
                .carId(carId)
                .carState(CarState.NOT_AVAILABLE)
                .build();
    }

    private void unlockCar(String carId) {
        redisTemplate.delete(carId);
    }

}
