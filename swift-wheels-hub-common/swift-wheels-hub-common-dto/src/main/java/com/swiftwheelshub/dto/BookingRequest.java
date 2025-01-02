package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BookingRequest(
        @NotNull(message = "Date of booking cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBooking,

        BookingState status,

        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Date from cannot be null")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateFrom,

        @NotNull(message = "Date to cannot be blank")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateTo,

        BigDecimal amount,

        BigDecimal rentalCarPrice,

        @NotNull(message = "Rental branch id cannot be null")
        Long rentalBranchId,

        Long returnBranchId
) {

    @Override
    public String toString() {
        return "BookingRequest{" + "\n" +
                "dateOfBooking=" + dateOfBooking + "\n" +
                "status=" + status +
                "previousCarId=" + carId + "\n" +
                "dateFrom=" + dateFrom + "\n" +
                "dateTo=" + dateTo + "\n" +
                "amount=" + amount + "\n" +
                "rentalCarPrice=" + rentalCarPrice + "\n" +
                "rentalBranchId=" + rentalBranchId + "\n" +
                "returnBranchId=" + returnBranchId + "\n" +
                "}";
    }

}
