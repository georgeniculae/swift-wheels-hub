package com.swiftwheelshub.lib.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.function.Consumer;

@UtilityClass
public class HttpRequestUtil {

    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_USERNAME = "X-USERNAME";
    private static final String HEADERS = "headers";

    public static HttpEntity<String> getHttpEntity(HttpServletRequest request) {
        final String authenticationHeader = extractAuthenticationTokenFromRequest(request);

        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(X_API_KEY, authenticationHeader);

        return new HttpEntity<>(HEADERS, headers);
    }

    public static Consumer<HttpHeaders> mutateHeaders(HttpServletRequest request) {
        return httpHeaders -> {
            httpHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add(X_API_KEY, HttpRequestUtil.extractAuthenticationTokenFromRequest(request));
        };
    }

    public static HttpEntity<Object> getHttpEntityWithBody(HttpServletRequest request, Object o) {
        final String authenticationHeader = extractAuthenticationTokenFromRequest(request);

        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(X_API_KEY, authenticationHeader);

        return new HttpEntity<>(o, headers);
    }

    public static String extractAuthenticationTokenFromRequest(HttpServletRequest request) {
        return request.getHeader(X_API_KEY);
    }

    public static String extractUsername(HttpServletRequest request) {
        return request.getHeader(X_USERNAME);
    }

}
