package com.swiftwheelshub.booking.util;

import com.swiftwheelshub.dto.BookingRequest;
import com.swiftwheelshub.dto.BookingResponse;
import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.CarRequest;
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

    public static void assertCar(Car car, CarRequest carRequest) {
        assertEquals(car.getMake(), carRequest.make());
        assertEquals(car.getModel(), carRequest.model());
        assertEquals(car.getBodyType().getDisplayName(), carRequest.bodyCategory().getDisplayName());
        assertEquals(car.getYearOfProduction(), carRequest.yearOfProduction());
        assertEquals(car.getColor(), carRequest.color());
        assertEquals(car.getMileage(), carRequest.mileage());
        assertEquals(car.getCarStatus().getDisplayName(), carRequest.carState().getDisplayName());
        assertEquals(car.getAmount(), carRequest.amount());
        assertEquals(car.getUrlOfImage(), carRequest.urlOfImage());
    }

    public static void assertBranch(Branch branch, BranchRequest branchRequest) {
        assertEquals(branch.getName(), branchRequest.name());
        assertEquals(branch.getAddress(), branchRequest.address());
    }

}
