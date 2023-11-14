package com.carrental.dto;

import com.carrental.entity.CarStatus;

public record CarForUpdate(
        Long carId,
        CarStatus carStatus
) {

    @Override
    public String toString() {
        return "CarForUpdate{" + "\n" +
                "carId=" + carId + "\n" +
                "carStatus=" + carStatus + "\n" +
                "}";
    }

}
