package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CarUpdateDetails(
        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Car phase cannot be null")
        CarPhase carPhase,

        @NotNull(message = "Receptionist employee id cannot be null")
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarUpdateDetails{" + "\n" +
                "carId=" + carId + "\n" +
                "carPhase=" + carPhase + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
