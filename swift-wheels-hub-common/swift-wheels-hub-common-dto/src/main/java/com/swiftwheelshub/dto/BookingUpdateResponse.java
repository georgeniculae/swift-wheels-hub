package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record BookingUpdateResponse(boolean isSuccessful) {
}
