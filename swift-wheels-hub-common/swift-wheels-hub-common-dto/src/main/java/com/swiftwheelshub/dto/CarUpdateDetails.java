package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Builder
public record CarUpdateDetails(
        @NotNull(message = "Car id cannot be null")
        Long carId,

        @NotNull(message = "Car phase cannot be null")
        CarState carState,

        @NonNull
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarUpdateDetails{" + "\n" +
                "carId=" + carId + "\n" +
                "carState=" + carState + "\n" +
                "returnBranchId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}
