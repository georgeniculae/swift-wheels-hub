package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record CarUpdateDetails(
        Long carId,
        CarState carState,
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarUpdateDetails{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
