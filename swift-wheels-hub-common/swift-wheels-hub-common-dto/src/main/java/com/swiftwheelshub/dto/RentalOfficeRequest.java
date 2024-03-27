package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record RentalOfficeRequest(
        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotEmpty(message = "Contact address cannot be empty")
        String contactAddress,

        @NotEmpty(message = "Phone number cannot be empty")
        String phoneNumber
) {

    @Override
    public String toString() {
        return "RentalOfficeRequest{" + "\n" +
                "name='" + name + "\n" +
                "contactAddress='" + contactAddress + "\n" +
                "phoneNumber='" + phoneNumber + "\n" +
                "}";
    }

}
