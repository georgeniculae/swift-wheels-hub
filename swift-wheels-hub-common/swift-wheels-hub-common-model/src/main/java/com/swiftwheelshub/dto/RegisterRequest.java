package com.swiftwheelshub.dto;

import com.swiftwheelshub.entity.Role;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record RegisterRequest(
        String username,
        String password,
        String firstName,
        String lastName,
        String email,
        String address,
        Role role,
        LocalDate dateOfBirth,
        boolean needsVerification
) {

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + "\n" +
                "password='" + password + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "email='" + email + "\n" +
                "address='" + address + "\n" +
                "role=" + role + "\n" +
                "dateOfBirth=" + dateOfBirth + "\n" +
                "}";
    }

}
