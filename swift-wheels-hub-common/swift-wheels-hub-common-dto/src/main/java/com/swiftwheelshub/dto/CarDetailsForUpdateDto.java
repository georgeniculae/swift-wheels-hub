package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record CarDetailsForUpdateDto(
        Long carId,
        CarState carState,
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarDetailsForUpdateDto{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
