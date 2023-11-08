package com.carrental.lib.security.apikey;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
@ConditionalOnBean(name = "apiKeyAuthenticationFilter")
public class ApiKeyAuthenticationManager {

    private final ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(apiKeyAuthenticationProvider));
    }

}
