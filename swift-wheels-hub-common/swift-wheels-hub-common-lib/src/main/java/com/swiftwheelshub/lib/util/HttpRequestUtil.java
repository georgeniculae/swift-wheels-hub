package com.swiftwheelshub.lib.util;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@UtilityClass
public class HttpRequestUtil {

    private static final String X_API_KEY = "X-API-KEY";
    private static final String X_ROLES = "X-ROLES";
    private static final String X_USERNAME = "X-USERNAME";
    private static final String X_EMAIL = "X-EMAIL";

    public static Consumer<HttpHeaders> setHttpHeaders(String apiKey, List<String> roles) {
        return httpHeaders -> {
            httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add(X_API_KEY, apiKey);
            httpHeaders.addAll(X_ROLES, roles);
        };
    }

    public static String extractUsername() {
        HttpServletRequest request = getRequest();

        return Optional.ofNullable(request.getHeader(X_USERNAME))
                .orElse(StringUtils.EMPTY);
    }

    public static String extractEmail() {
        HttpServletRequest request = getRequest();

        return Optional.ofNullable(request.getHeader(X_EMAIL))
                .orElse(StringUtils.EMPTY);
    }

    private static HttpServletRequest getRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        return Optional.ofNullable(requestAttributes)
                .map(ServletRequestAttributes::getRequest)
                .orElseThrow(() -> new SwiftWheelsHubException("Request attributes are null"));
    }

}
