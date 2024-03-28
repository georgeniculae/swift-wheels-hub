package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CarUpdateDetails(
        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Car state cannot be null")
        CarState carState,

        @NotNull(message = "Receptionist employee id cannot be null")
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
