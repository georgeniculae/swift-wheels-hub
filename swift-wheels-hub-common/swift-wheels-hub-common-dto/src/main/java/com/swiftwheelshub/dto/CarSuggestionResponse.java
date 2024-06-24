package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record CarSuggestionResponse(
        @NotEmpty
        String carSuggested,

        @NotEmpty
        String justification) {
}
