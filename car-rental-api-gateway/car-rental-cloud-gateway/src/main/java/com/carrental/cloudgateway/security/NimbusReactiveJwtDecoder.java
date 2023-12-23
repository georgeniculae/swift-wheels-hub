package com.carrental.cloudgateway.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NimbusReactiveJwtDecoder {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkUri;

    @Bean
    public org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder nimbusReactiveJwtDecoder() {
        return org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withJwkSetUri(jwkUri).build();
    }

}
