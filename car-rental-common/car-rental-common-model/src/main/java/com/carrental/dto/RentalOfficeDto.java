package com.carrental.dto;

import jakarta.validation.constraints.NotEmpty;

public record RentalOfficeDto(
        Long id,

        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotEmpty(message = "Contact address domain cannot be empty")
        String contactAddress,

        String logoType
) {

    @Override
    public String toString() {
        return "RentalOfficeDto{" + "\n" +
                "id=" + id + "\n" +
                "name='" + name + "\n" +
                "contactAddress='" + contactAddress + "\n" +
                "logoType='" + logoType + "\n" +
                "}";
    }

}
