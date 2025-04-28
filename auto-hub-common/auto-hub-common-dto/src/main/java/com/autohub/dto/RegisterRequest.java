package com.autohub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegisterRequest(
        @NotEmpty(message = "Username cannot be empty")
        String username,

        @NotEmpty(message = "Password cannot be empty")
        String password,

        @NotEmpty(message = "Email cannot be empty")
        String email,

        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotEmpty(message = "Address cannot be empty")
        String address,

        @NotNull(message = "Date of birth cannot be null")
        LocalDate dateOfBirth,

        boolean needsEmailVerification
) {

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + "\n" +
                "password='" + password + "\n" +
                "email='" + email + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "address='" + address + "\n" +
                "dateOfBirth=" + dateOfBirth + "\n" +
                "needsEmailVerification" + needsEmailVerification + "\n" +
                "}";
    }

}
