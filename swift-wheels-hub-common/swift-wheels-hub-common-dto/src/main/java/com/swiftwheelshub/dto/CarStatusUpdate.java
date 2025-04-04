package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CarStatusUpdate(
        @NotNull
        Long carId,

        @NotNull
        CarState carState
) {

    @Override
    public String toString() {
        return "CarStatusUpdate{" + "\n" +
                "previousCarId=" + carId + "\n" +
                "actualCarId=" + carState + "\n" +
                "}";
    }

}
