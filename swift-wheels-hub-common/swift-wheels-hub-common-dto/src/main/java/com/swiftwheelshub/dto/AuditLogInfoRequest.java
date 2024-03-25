package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AuditLogInfoRequest(
        @NotEmpty(message = "Method name cannot be empty")
        String methodName,

        String username,

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
