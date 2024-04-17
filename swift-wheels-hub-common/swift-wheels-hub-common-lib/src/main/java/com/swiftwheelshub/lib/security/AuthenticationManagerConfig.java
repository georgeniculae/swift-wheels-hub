package com.swiftwheelshub.lib.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "apikey", name = "secret")
public class AuthenticationManagerConfig {

    private final ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(List.of(apiKeyAuthenticationProvider));
    }

}
