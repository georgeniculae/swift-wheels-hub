package com.carrental.booking.util;

import com.carrental.dto.BookingDto;
import com.carrental.dto.BranchDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarStatusEnum;
import com.carrental.entity.BodyType;
import com.carrental.entity.Booking;
import com.carrental.entity.Branch;
import com.carrental.entity.Car;
import com.carrental.entity.CarStatus;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertBooking(Booking booking, BookingDto bookingDto) {
        assertEquals(booking.getDateOfBooking(), bookingDto.getDateOfBooking());
        assertEquals(booking.getDateFrom(), bookingDto.getDateFrom());
        assertEquals(booking.getDateTo(), bookingDto.getDateTo());
        assertEquals(booking.getAmount(), Objects.requireNonNull(bookingDto.getAmount()).doubleValue());
    }

    public static void assertCar(Car car, CarDto carDto) {
        assertEquals(car.getMake(), carDto.getMake());
        assertEquals(car.getModel(), carDto.getModel());
        assertBodyType(car.getBodyType(), Objects.requireNonNull(carDto.getBodyType()));
        assertEquals(car.getYearOfProduction(), carDto.getYearOfProduction());
        assertEquals(car.getColor(), carDto.getColor());
        assertEquals(car.getMileage(), carDto.getMileage());
        assertCarStatus(car.getCarStatus(), Objects.requireNonNull(carDto.getCarStatus()));
        assertEquals(car.getAmount(), Objects.requireNonNull(carDto.getAmount()).doubleValue());
        assertEquals(car.getUrlOfImage(), carDto.getUrlOfImage());
    }

    public static void assertBranch(Branch branch, BranchDto branchDto) {
        assertEquals(branch.getName(), branchDto.getName());
        assertEquals(branch.getAddress(), branchDto.getAddress());
    }

    private static void assertBodyType(BodyType bodyType, CarDto.BodyTypeEnum bodyTypeEnum) {
        assertEquals(bodyType.getDisplayName(), bodyTypeEnum.getValue());
    }

    private static void assertCarStatus(CarStatus carStatus, CarStatusEnum carStatusEnum) {
        assertEquals(carStatus.getDisplayName(), carStatusEnum.getValue());
    }

}
