package com.autohub.dto.common;

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
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "}";
    }

}
