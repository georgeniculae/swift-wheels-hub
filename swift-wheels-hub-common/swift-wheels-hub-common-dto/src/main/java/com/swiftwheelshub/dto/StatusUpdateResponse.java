package com.swiftwheelshub.dto;

import lombok.Builder;

@Builder
public record StatusUpdateResponse(boolean isUpdateSuccessful) {
}
