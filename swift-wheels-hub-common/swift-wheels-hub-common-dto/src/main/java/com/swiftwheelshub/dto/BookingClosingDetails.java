package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookingClosingDetails(
        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        @NotNull(message = "Receptionist employee id cannot be null")
        Long receptionistEmployeeId,

        @NotNull(message = "Car state cannot be null")
        CarState carState
) {

    @Override
    public String toString() {
        return "BookingClosingDetails{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
