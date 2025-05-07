package com.autohub.dto.customer;

import lombok.Builder;

@Builder
public record CustomerInfo(
        String username,
        String email
) {
}
