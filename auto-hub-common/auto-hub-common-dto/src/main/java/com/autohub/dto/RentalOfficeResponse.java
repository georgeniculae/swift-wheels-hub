package com.autohub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record RentalOfficeResponse(
        Long id,

        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotEmpty(message = "Contact address domain cannot be empty")
        String contactAddress,

        @NotEmpty(message = "Phone number cannot be empty")
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
