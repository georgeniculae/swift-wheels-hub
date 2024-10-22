package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BookingClosingDetails(
        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        @NotNull(message = "Receptionist employee id cannot be null")
        Long receptionistEmployeeId,

        @NotNull(message = "Car phase cannot be null")
        CarPhase carPhase
) {

    @Override
    public String toString() {
        return "BookingClosingDetails{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "carState=" + carPhase + "\n" +
                "}";
    }

}
