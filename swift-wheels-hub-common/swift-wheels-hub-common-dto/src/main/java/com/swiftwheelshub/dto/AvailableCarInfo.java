package com.swiftwheelshub.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AvailableCarInfo(
        Long id,
        Long actualBranchId,
        BigDecimal amount
) {
}
