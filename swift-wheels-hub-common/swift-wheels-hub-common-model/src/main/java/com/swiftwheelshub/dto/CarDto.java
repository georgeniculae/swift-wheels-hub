package com.swiftwheelshub.dto;

import com.swiftwheelshub.entity.BodyType;
import com.swiftwheelshub.entity.CarStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotEmpty;

public record CarDto(
        Long id,

        @NotEmpty(message = "Make cannot be empty")
        String make,

        @NotEmpty(message = "Model cannot be null")
        String model,

        @Enumerated(EnumType.STRING)
        BodyType bodyType,

        int yearOfProduction,

        String color,

        int mileage,

        @Enumerated(EnumType.STRING)
        CarStatus carStatus,

        Double amount,

        Long originalBranchId,

        Long actualBranchId,

        String urlOfImage
) {

    @Override
    public String toString() {
        return "CarDto{" + "\n" +
                "id=" + id +
                "make='" + make + "\n" +
                "model='" + model + "\n" +
                "bodyType=" + bodyType + "\n" +
                "yearOfProduction=" + yearOfProduction + "\n" +
                "color='" + color + "\n" +
                "mileage=" + mileage + "\n" +
                "carStatus=" + carStatus + "\n" +
                "amount=" + amount + "\n" +
                "originalBranchId=" + originalBranchId + "\n" +
                "actualBranchId=" + actualBranchId + "\n" +
                "urlOfImage='" + urlOfImage + "\n" +
                "}";
    }

}
