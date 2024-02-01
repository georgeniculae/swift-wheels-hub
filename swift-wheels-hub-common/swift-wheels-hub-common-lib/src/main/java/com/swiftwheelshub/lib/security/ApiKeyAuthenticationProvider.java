package com.swiftwheelshub.lib.security;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    @Value("${apikey.secret}")
    private String apiKeySecret;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKey = authentication.getPrincipal().toString();

        if (apiKeySecret.equals(apiKey)) {
            return new ApiKeyAuthenticationToken(getRoles(authentication), apiKey, true);
        }

        throw new SwiftWheelsHubException("API Key is invalid");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

    private List<SimpleGrantedAuthority> getRoles(Authentication authentication) {
        return authentication.getAuthorities()
                .stream()
                .map(grantedAuthority -> new SimpleGrantedAuthority(grantedAuthority.getAuthority()))
                .toList();
    }

}
