package com.swiftwheelshub.dto;

import com.swiftwheelshub.entity.CarStatus;
import lombok.Builder;

@Builder
public record CarDetailsForUpdateDto(
        Long carId,
        CarStatus carStatus,
        Long receptionistEmployeeId
) {

    @Override
    public String toString() {
        return "CarDetailsForUpdateDto{" + "\n" +
                "carId=" + carId + "\n" +
                "carStatus=" + carStatus + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "}";
    }

}