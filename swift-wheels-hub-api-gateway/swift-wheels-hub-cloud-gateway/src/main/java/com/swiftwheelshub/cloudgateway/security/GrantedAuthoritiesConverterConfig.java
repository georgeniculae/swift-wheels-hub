package com.swiftwheelshub.cloudgateway.security;

import com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtGrantedAuthoritiesConverterAdapter;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class GrantedAuthoritiesConverterConfig {

    private static final String REALM_ACCESS = "realm_access";
    private static final String ROLES = "roles";

    @Bean
    public Converter<Jwt, Flux<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return new ReactiveJwtGrantedAuthoritiesConverterAdapter(authoritiesConverter());
    }

    @Bean
    @SuppressWarnings("unchecked")
    public JwtGrantedAuthorityConverter authoritiesConverter() {
        return source -> ((LinkedTreeMap<String, List<String>>) source.getClaims().get(REALM_ACCESS))
                .get(ROLES)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public interface JwtGrantedAuthorityConverter extends Converter<Jwt, Collection<GrantedAuthority>> {
    }

}
