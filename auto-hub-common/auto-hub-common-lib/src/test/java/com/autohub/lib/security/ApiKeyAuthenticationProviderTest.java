package com.autohub.lib.security;

import com.autohub.exception.AutoHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationProviderTest {

    @InjectMocks
    private ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

    @Test
    void authenticateTest_success() {
        ReflectionTestUtils.setField(apiKeyAuthenticationProvider, "apiKeySecret", "apikey");

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");

        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "apikey");

        Authentication authenticated = apiKeyAuthenticationProvider.authenticate(apiKeyAuthenticationToken);
        assertTrue(authenticated.isAuthenticated());
    }

    @Test
    void authenticateTest_noApiKey() {
        ReflectionTestUtils.setField(apiKeyAuthenticationProvider, "apiKeySecret", "apikey");

        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority("user");

        ApiKeyAuthenticationToken apiKeyAuthenticationToken =
                new ApiKeyAuthenticationToken(List.of(simpleGrantedAuthority), "test");

        assertThrows(AutoHubException.class, () -> apiKeyAuthenticationProvider.authenticate(apiKeyAuthenticationToken));
    }

}
