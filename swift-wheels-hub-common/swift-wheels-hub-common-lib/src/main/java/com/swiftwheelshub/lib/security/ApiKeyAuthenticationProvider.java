package com.swiftwheelshub.lib.security;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    @Value("${authentication.secret}")
    private String apiKeySecret;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKey = authentication.getPrincipal().toString();

        if (apiKeySecret.equals(apiKey)) {
            return new ApiKeyAuthenticationToken(apiKey, true);
        }

        throw new SwiftWheelsHubException("API Key is invalid");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
