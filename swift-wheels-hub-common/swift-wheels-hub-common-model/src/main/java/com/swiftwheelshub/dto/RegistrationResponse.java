package com.swiftwheelshub.dto;

import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
public record RegistrationResponse(
        String username,
        String firstName,
        String lastName,
        String email,
        ZonedDateTime registrationDate
) {
}
