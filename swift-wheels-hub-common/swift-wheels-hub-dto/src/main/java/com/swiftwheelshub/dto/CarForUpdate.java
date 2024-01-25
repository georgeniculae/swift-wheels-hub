package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record CarForUpdate(
        Long carId,
        CarState carState
) {

    @Override
    public String toString() {
        return "CarForUpdate{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
