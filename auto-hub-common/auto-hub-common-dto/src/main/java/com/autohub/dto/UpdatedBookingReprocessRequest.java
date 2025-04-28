package com.autohub.dto;

import lombok.Builder;
import org.springframework.lang.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record UpdatedBookingReprocessRequest(
        Long id,

        @NonNull
        LocalDate dateOfBooking,

        BookingState status,

        String customerUsername,

        String customerEmail,

        @NonNull
        Long actualCarId,

        Long previousCarId,

        @NonNull
        LocalDate dateFrom,

        @NonNull
        LocalDate dateTo,

        BigDecimal rentalCarPrice,

        Long rentalBranchId,

        Long returnBranchId,

        boolean isCarChanged
) {
}
