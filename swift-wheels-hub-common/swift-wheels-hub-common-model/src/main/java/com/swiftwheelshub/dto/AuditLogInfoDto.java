package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

import java.util.List;

@Builder
public record AuditLogInfoDto(@NotEmpty(message = "Method name cannot be empty")
                              String methodName,
                              String username,
                              List<String> parametersValues) {

    @Override
    public String toString() {
        return "AuditLogInfoDto{" + "\n" +
                "methodName=" + methodName + "\n" +
                "username=" + username + "\n" +
                "parametersValues=" + parametersValues + "\n" +
                "}";
    }

}