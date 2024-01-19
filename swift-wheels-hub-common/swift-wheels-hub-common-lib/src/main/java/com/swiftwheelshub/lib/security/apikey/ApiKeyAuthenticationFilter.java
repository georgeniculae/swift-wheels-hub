package com.swiftwheelshub.lib.security.apikey;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "authentication", name = "type", havingValue = "apikey")
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private final static String API_KEY_HEADER = "X-API-KEY";
    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(API_KEY_HEADER);

        if (ObjectUtils.isNotEmpty(authorization)) {
            ApiKeyAuthenticationToken apiKeyAuthenticationToken = new ApiKeyAuthenticationToken(authorization);
            Authentication authenticate = authenticationManager.authenticate(apiKeyAuthenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        }

        filterChain.doFilter(request, response);
    }

}
