package com.swiftwheelshub.lib.util;

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

    public static Consumer<HttpHeaders> setHttpHeaders(String apiKey, List<String> roles) {
        return httpHeaders -> {
            httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
            httpHeaders.add(X_API_KEY, apiKey);
            httpHeaders.addAll(X_ROLES, roles);
        };
    }

    public static String extractUsername() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = Optional.ofNullable(requestAttributes)
                .orElseThrow()
                .getRequest();

        return Optional.ofNullable(request.getHeader(X_USERNAME))
                .orElse(StringUtils.EMPTY);
    }

}
