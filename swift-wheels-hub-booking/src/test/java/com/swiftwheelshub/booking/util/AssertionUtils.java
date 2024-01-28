package com.swiftwheelshub.booking.util;

import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.BranchDto;
import com.swiftwheelshub.dto.CarDto;
import com.swiftwheelshub.entity.Booking;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertBooking(Booking booking, BookingRequest bookingRequest) {
        assertEquals(booking.getDateOfBooking(), bookingRequest.dateOfBooking());
        assertEquals(booking.getDateFrom(), bookingRequest.dateFrom());
        assertEquals(booking.getDateTo(), bookingRequest.dateTo());
        assertEquals(booking.getAmount(), Objects.requireNonNull(bookingRequest.amount()).doubleValue());
    }

    public static void assertBooking(Booking booking, BookingResponse bookingResponse) {
        assertEquals(booking.getDateOfBooking(), bookingResponse.dateOfBooking());
        assertEquals(booking.getDateFrom(), bookingResponse.dateFrom());
        assertEquals(booking.getDateTo(), bookingResponse.dateTo());
        assertEquals(booking.getAmount(), Objects.requireNonNull(bookingResponse.amount()).doubleValue());
    }

    public static void assertCar(Car car, CarDto carDto) {
        assertEquals(car.getMake(), carDto.make());
        assertEquals(car.getModel(), carDto.model());
        assertEquals(car.getBodyType().getDisplayName(), carDto.bodyCategory().getDisplayName());
        assertEquals(car.getYearOfProduction(), carDto.yearOfProduction());
        assertEquals(car.getColor(), carDto.color());
        assertEquals(car.getMileage(), carDto.mileage());
        assertEquals(car.getCarStatus().getDisplayName(), carDto.carState().getDisplayName());
        assertEquals(car.getAmount(), carDto.amount());
        assertEquals(car.getUrlOfImage(), carDto.urlOfImage());
    }

    public static void assertBranch(Branch branch, BranchDto branchDto) {
        assertEquals(branch.getName(), branchDto.name());
        assertEquals(branch.getAddress(), branchDto.address());
    }

}
