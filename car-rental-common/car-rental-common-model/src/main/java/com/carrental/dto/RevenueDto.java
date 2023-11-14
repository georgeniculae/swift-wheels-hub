package com.carrental.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RevenueDto(
        Long id,

        @NotNull(message = "Date of revenue cannot be null")
        LocalDate dateOfRevenue,

        @NotNull(message = "Amount from booking cannot be null")
        Double amountFromBooking
) {

    @Override
    public String toString() {
        return "RevenueDto{" + "\n" +
                "id=" + id + "\n" +
                "dateOfRevenue=" + dateOfRevenue + "\n" +
                "amountFromBooking=" + amountFromBooking + "\n" +
                "}";
    }
}
