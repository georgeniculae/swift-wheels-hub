package com.swiftwheelshub.dto;

import com.swiftwheelshub.entity.Role;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record UserDto(
        @NotBlank
        String username,

        @NotBlank
        String password,

        @Enumerated(EnumType.STRING)
        Role role,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        @NotBlank
        String email,

        @NotNull
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate dateOfBirth,

        @NotBlank
        String address
) {

    @Override
    public String toString() {
        return "UserDto{" + "\n" +
                "username='" + username + "\n" +
                "password='" + password + "\n" +
                "role=" + role +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "email='" + email + "\n" +
                "dateOfBirth=" + dateOfBirth + "\n" +
                "address='" + address + "\n" +
                "}";
    }
}
