package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record ImageResponse(
        Long id,

        String name,

        String type,

        byte[] content
) {
}
