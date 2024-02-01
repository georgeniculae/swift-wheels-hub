package com.swiftwheelshub.lib.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

@Transient
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyAuthenticationToken(List<SimpleGrantedAuthority> grantedAuthorities, String apiKey) {
        super(grantedAuthorities);
        super.setAuthenticated(false);
        this.apiKey = apiKey;
    }

    public ApiKeyAuthenticationToken(List<SimpleGrantedAuthority> grantedAuthorities, String apiKey, boolean isAuthenticated) {
        super(grantedAuthorities);
        super.setAuthenticated(isAuthenticated);
        this.apiKey = apiKey;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return apiKey;
    }

}
