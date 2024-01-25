package com.swiftwheelshub.lib.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Transient;
import org.springframework.security.core.authority.AuthorityUtils;

@Transient
public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;

    public ApiKeyAuthenticationToken(String apiKey, boolean isAuthenticated) {
        super(AuthorityUtils.NO_AUTHORITIES);
        super.setAuthenticated(isAuthenticated);
        this.apiKey = apiKey;
    }

    public ApiKeyAuthenticationToken(String apiKey) {
        super(AuthorityUtils.NO_AUTHORITIES);
        super.setAuthenticated(false);
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
