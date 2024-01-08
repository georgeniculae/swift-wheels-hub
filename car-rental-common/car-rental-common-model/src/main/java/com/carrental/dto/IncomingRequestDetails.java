package com.carrental.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Map;

@Builder
public record IncomingRequestDetails(
        @NotBlank
        String path,

        @NotBlank
        String method,

        Map<String, String> headers,

        Map<String, String> queryParams,

        String body
) {
}
