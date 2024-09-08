package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserUpdateRequest(
        @NotEmpty(message = "Username cannot be empty")
        String username,

        @NotEmpty(message = "Email cannot be empty")
        String email,

        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotEmpty(message = "Address cannot be empty")
        String address,

        @NotNull(message = "Date of birth cannot be null")
        LocalDate dateOfBirth
) {

    @Override
    public String toString() {
        return "UserUpdateRequest{" + "\n" +
                "username=" + username + "\n" +
                "email=" + email + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName=" + lastName + "\n" +
                "address=" + address + "\n" +
                "dateOfBirth=" + dateOfBirth + "\n" +
                "}";
    }

}
