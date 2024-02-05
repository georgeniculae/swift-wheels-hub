package com.swiftwheelshub.lib.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;

@UtilityClass
public class HttpRequestUtil {

    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_USERNAME = "X-USERNAME";
    private static final String X_ROLES = "X-ROLES";
    private static final String HEADERS = "headers";

    public static HttpEntity<String> getHttpEntity(HttpServletRequest request) {
        final String authenticationHeader = extractAuthenticationToken(request);

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
            httpHeaders.add(X_API_KEY, extractAuthenticationToken(request));
            httpHeaders.addAll(X_ROLES, extractRoles(request));
        };
    }

    public static HttpEntity<Object> getHttpEntityWithBody(HttpServletRequest request, Object o) {
        final String authenticationHeader = extractAuthenticationToken(request);

        HttpHeaders headers = new HttpHeaders();

        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(X_API_KEY, authenticationHeader);

        return new HttpEntity<>(o, headers);
    }

    public static String extractAuthenticationToken(HttpServletRequest request) {
        return request.getHeader(X_API_KEY);
    }

    public static List<String> extractRoles(HttpServletRequest request) {
        Enumeration<String> headers = request.getHeaders(X_ROLES);

        if (ObjectUtils.isEmpty(headers)) {
            return List.of();
        }

        return Collections.list(headers);
    }

    public static String extractUsername(HttpServletRequest request) {
        return request.getHeader(X_USERNAME);
    }

}
