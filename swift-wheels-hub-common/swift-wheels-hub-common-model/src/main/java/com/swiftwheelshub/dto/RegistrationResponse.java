package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record RegistrationResponse(
        String username,
        String firstName,
        String lastName,
        String email,
        String address,
        String registrationDate
) {
}
