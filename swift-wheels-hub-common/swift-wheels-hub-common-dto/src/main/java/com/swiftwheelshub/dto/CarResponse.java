package com.swiftwheelshub.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record CarResponse(
        Long id,

        @NotEmpty(message = "Make cannot be empty")
        String make,

        @NotEmpty(message = "Model cannot be null")
        String model,

        @Enumerated(EnumType.STRING)
        BodyCategory bodyCategory,

        int yearOfProduction,

        String color,

        int mileage,

        @Enumerated(EnumType.STRING)
        CarState carState,

        Double amount,

        Long originalBranchId,

        Long actualBranchId
) {

    @Override
    public String toString() {
        return "CarResponse{" + "\n" +
                "id=" + id +
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
