package com.swiftwheelshub.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record CarRequest(
        @NotEmpty(message = "Make cannot be empty")
        String make,

        @NotEmpty(message = "Model cannot be null")
        String model,

        @Enumerated(EnumType.STRING)
        @NotNull(message = "Body category cannot be null")
        BodyCategory bodyCategory,

        @NotNull(message = "Year of production cannot be null")
        Integer yearOfProduction,

        @NotNull(message = "Color cannot be null")
        String color,

        @NotNull(message = "Mileage cannot be null")
        Integer mileage,

        @Enumerated(EnumType.STRING)
        CarState carState,

        @NotNull(message = "Amount cannot be null")
        BigDecimal amount,

        @NotNull(message = "Original branch cannot be null")
        Long originalBranchId,

        @NotNull(message = "Actual branch cannot be null")
        Long actualBranchId
) {

    @Override
    public String toString() {
        return "CarRequest{" + "\n" +
                "make='" + make + "\n" +
                "model='" + model + "\n" +
                "bodyCategory=" + bodyCategory + "\n" +
                "yearOfProduction=" + yearOfProduction + "\n" +
                "color='" + color + "\n" +
                "mileage=" + mileage + "\n" +
                "carState=" + carState + "\n" +
                "amount=" + amount + "\n" +
                "originalBranchId=" + originalBranchId + "\n" +
                "actualBranchId=" + actualBranchId + "\n" +
                "}";
    }

}
