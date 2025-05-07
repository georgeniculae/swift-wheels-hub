package com.autohub.lib.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.Transient;

import java.util.Collection;

@Transient
public final class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyAuthenticationToken(Collection<? extends GrantedAuthority> grantedAuthorities, String apiKey) {
        super(grantedAuthorities);
        super.setAuthenticated(false);
        this.apiKey = apiKey;
    }

    public ApiKeyAuthenticationToken(ApiKeyAuthenticationToken apiKeyAuthenticationToken) {
        super(apiKeyAuthenticationToken.getAuthorities());
        super.setAuthenticated(true);
        this.apiKey = apiKeyAuthenticationToken.getPrincipal().toString();
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
