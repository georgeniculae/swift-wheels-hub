package com.carrental.cloudgateway.security;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class GrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final String ROLES = "roles";
    private static final String CLAIM_REALM_ACCESS = "realm_access";
    private static final String RESOURCE_ACCESS = "resource_access";

    @Value("${keycloak.clientId}")
    private String clientId;

    private final Converter<Jwt, Collection<GrantedAuthority>> authoritiesConverter;

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt source) {
        var realmRoles = realmRoles(source);
        var clientRoles = clientRoles(source, clientId);

        Collection<GrantedAuthority> authorities = Stream.concat(realmRoles.stream(), clientRoles.stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        authorities.addAll(defaultGrantedAuthorities(source));

        return authorities;
    }

    private Collection<GrantedAuthority> defaultGrantedAuthorities(Jwt jwt) {
        return Optional.ofNullable(authoritiesConverter.convert(jwt))
                .orElse(Collections.emptySet());
    }

    @SuppressWarnings("unchecked")
    private List<String> realmRoles(Jwt jwt) {
        return Optional.ofNullable(jwt.getClaimAsMap(CLAIM_REALM_ACCESS))
                .map(realmAccess -> (List<String>) realmAccess.get(ROLES))
                .orElse(Collections.emptyList());
    }

    @SuppressWarnings("unchecked")
    private List<String> clientRoles(Jwt jwt, String clientId) {
        if (ObjectUtils.isEmpty(clientId)) {
            return Collections.emptyList();
        }

        return Optional.ofNullable(jwt.getClaimAsMap(RESOURCE_ACCESS))
                .map(resourceAccess -> (Map<String, List<String>>) resourceAccess.get(clientId))
                .map(clientAccess -> clientAccess.get(ROLES))
                .orElse(Collections.emptyList());
    }

}
