package com.carrental.booking.util;

import com.carrental.dto.BookingDto;
import com.carrental.dto.BranchDto;
import com.carrental.dto.CarDto;
import com.carrental.entity.Booking;
import com.carrental.entity.Branch;
import com.carrental.entity.Car;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertBooking(Booking booking, BookingDto bookingDto) {
        assertEquals(booking.getDateOfBooking(), bookingDto.dateOfBooking());
        assertEquals(booking.getDateFrom(), bookingDto.dateFrom());
        assertEquals(booking.getDateTo(), bookingDto.dateTo());
        assertEquals(booking.getAmount(), Objects.requireNonNull(bookingDto.amount()).doubleValue());
    }

    public static void assertCar(Car car, CarDto carDto) {
        assertEquals(car.getMake(), carDto.make());
        assertEquals(car.getModel(), carDto.model());
        assertEquals(car.getBodyType(), carDto.bodyType());
        assertEquals(car.getYearOfProduction(), carDto.yearOfProduction());
        assertEquals(car.getColor(), carDto.color());
        assertEquals(car.getMileage(), carDto.mileage());
        assertEquals(car.getCarStatus(), carDto.carStatus());
        assertEquals(car.getAmount(), carDto.amount());
        assertEquals(car.getUrlOfImage(), carDto.urlOfImage());
    }

    public static void assertBranch(Branch branch, BranchDto branchDto) {
        assertEquals(branch.getName(), branchDto.name());
        assertEquals(branch.getAddress(), branchDto.address());
    }

}
