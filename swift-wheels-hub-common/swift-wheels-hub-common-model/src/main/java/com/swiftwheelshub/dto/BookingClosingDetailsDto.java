package com.swiftwheelshub.dto;

import com.swiftwheelshub.entity.CarStatus;

public record BookingClosingDetailsDto(
        Long bookingId,
        Long receptionistEmployeeId,
        CarStatus carStatus
) {

    @Override
    public String toString() {
        return "BookingClosingDetailsDto{" + "\n" +
                "bookingId=" + bookingId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "carStatus=" + carStatus + "\n" +
                "}";
    }

}
