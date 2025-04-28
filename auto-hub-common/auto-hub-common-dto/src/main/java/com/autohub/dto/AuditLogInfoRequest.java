package com.autohub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AuditLogInfoRequest(
        @NotEmpty(message = "Method name cannot be empty")
        String methodName,

        String username,

        @NotNull(message = "Timestamp cannot be null")
        LocalDateTime timestamp,

        List<String> parametersValues
) {

    @Override
    public String toString() {
        return "AuditLogInfoRequest{" + "\n" +
                "methodName=" + methodName + "\n" +
                "username=" + username + "\n" +
                "timestamp=" + timestamp + "\n" +
                "parametersValues=" + parametersValues + "\n" +
                "}";
    }

}
