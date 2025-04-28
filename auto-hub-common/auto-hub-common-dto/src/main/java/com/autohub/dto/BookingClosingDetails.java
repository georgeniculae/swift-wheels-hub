package com.autohub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookingClosingDetails(
        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        @NotNull(message = "Receptionist employee id cannot be null")
        Long returnBranchId
) {

    @Override
    public String toString() {
        return "BookingClosingDetails{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "returnBranchId=" + returnBranchId + "\n" +
                "}";
    }

}
