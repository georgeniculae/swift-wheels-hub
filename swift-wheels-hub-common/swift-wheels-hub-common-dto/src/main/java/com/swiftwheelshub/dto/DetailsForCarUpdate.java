package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record DetailsForCarUpdate(
        Long carId,
        CarState carState,
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "DetailsForCarUpdate{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
