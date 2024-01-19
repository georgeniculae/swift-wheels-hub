package com.swiftwheelshub.cloudgateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import reactor.core.publisher.Flux;

@Configuration
public class GrantedAuthoritiesConverterConfig {

    @Bean
    public JwtGrantedAuthoritiesConverter authoritiesConverter() {
        return new JwtGrantedAuthoritiesConverter();
    }

    @Bean
    public Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return new ReactiveJwtGrantedAuthoritiesConverterAdapter(authoritiesConverter());
    }

}
