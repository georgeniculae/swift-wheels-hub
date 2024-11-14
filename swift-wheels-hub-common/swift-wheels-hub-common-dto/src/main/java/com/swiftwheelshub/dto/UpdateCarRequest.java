package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCarRequest(
        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Car state cannot be null")
        CarState carState
) {

    @Override
    public String toString() {
        return "UpdateCarRequest{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
