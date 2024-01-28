package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record CarForUpdateDetails(
        Long carId,
        CarState carState,
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarForUpdateDetails{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
