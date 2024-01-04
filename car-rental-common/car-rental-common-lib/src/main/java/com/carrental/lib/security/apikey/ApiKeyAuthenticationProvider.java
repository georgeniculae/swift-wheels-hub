package com.carrental.lib.security.apikey;

import com.carrental.exception.CarRentalException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnBean(name = "apiKeyAuthenticationFilter")
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    @Value(("${authentication.secret}"))
    private String apiKeySecret;

    @Override
    public Authentication authenticate(Authentication authentication) {
        String apiKey = (String) authentication.getPrincipal();

        if (apiKeySecret.equals(apiKey)) {
            return new ApiKeyAuthenticationToken(apiKey, true);
        }

        throw new CarRentalException("API Key is invalid");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
