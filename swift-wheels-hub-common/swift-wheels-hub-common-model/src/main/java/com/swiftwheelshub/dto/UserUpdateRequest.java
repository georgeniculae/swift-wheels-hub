package com.swiftwheelshub.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserUpdateRequest(
        String username,
        String firstName,
        String lastName,
        String address,
        LocalDate dateOfBirth
) {
}
