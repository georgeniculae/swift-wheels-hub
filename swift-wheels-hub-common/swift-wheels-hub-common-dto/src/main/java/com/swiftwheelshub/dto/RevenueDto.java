package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
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