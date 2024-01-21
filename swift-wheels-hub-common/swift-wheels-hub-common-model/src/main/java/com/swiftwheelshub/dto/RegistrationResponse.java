package com.swiftwheelshub.dto;

import lombok.Builder;

import java.util.Date;

@Builder
public record RegistrationResponse(
        String username,
        String firstName,
        String lastName,
        String email,
        Date registrationDate
) {
}
