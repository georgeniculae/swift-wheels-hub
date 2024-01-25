package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record BookingClosingDetailsDto(
        Long bookingId,
        Long receptionistEmployeeId,
        CarState carState
) {

    @Override
    public String toString() {
        return "BookingClosingDetailsDto{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
