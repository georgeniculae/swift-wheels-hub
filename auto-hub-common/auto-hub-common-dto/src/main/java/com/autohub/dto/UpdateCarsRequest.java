package com.autohub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateCarsRequest(
        @NotNull(message = "Car id cannot be null")
        Long previousCarId,

        @NotNull(message = "Car state cannot be null")
        Long actualCarId
) {

    @Override
    public String toString() {
        return "UpdateCarRequest{" + "\n" +
                "previousCarId=" + previousCarId + "\n" +
                "actualCarId=" + actualCarId + "\n" +
                "}";
    }

}
