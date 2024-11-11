package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record BookingRollbackResponse(boolean isSuccessful, Long bookingId) {
}
