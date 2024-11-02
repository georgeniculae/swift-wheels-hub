package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record CustomerInfo(
        String username,
        String email
) {
}
