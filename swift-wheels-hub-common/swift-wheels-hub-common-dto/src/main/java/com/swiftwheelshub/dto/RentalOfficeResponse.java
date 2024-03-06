package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record RentalOfficeResponse(
        Long id,

        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotEmpty(message = "Contact address domain cannot be empty")
        String contactAddress,

        String phoneNumber
) {

    @Override
    public String toString() {
        return "RentalOfficeResponse{" + "\n" +
                "id=" + id + "\n" +
                "name='" + name + "\n" +
                "contactAddress='" + contactAddress + "\n" +
                "phoneNumber='" + phoneNumber + "\n" +
                "}";
    }

}
