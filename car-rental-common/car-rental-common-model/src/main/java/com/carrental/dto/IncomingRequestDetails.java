package com.carrental.dto;

import lombok.Builder;
import org.springframework.http.HttpHeaders;
import org.springframework.util.MultiValueMap;

@Builder
public record IncomingRequestDetails(
        String path,
        String method,
        HttpHeaders headers,
        MultiValueMap<String, String> queryParams,
        String body
) {
}