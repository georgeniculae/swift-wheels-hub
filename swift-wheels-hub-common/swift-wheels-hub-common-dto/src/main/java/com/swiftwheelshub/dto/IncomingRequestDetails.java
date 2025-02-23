package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.Map;

@Builder
public record IncomingRequestDetails(
        @NotBlank(message = "Path cannot be blank")
        String path,

        @NotBlank(message = "Method cannot be blank")
        String method,

        Map<String, String> headers,

        Map<String, String> queryParams,

        String body
) {

    @Override
    public String toString() {
        return "IncomingRequestDetails{" + "\n" +
                "path=" + path + "\n" +
                "method=" + method + "\n" +
                "headers=" + headers + "\n" +
                "queryParams=" + queryParams + "\n" +
                "body=" + body + "\n" +
                "}";
    }

}
