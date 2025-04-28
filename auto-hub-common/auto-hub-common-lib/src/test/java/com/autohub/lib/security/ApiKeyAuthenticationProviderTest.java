package com.autohub.lib.security;

import com.autohub.exception.AutoHubException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiKeyAuthenticationProviderTest {

    @InjectMocks
    private ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;

    @Mock
    private Authentication authentication;

    @Test
    void authenticateTest_success() {
        String apikey = "apikey";
        ReflectionTestUtils.setField(apiKeyAuthenticationProvider, "apiKeySecret", apikey);

        when(authentication.getPrincipal()).thenReturn(apikey);

        Authentication authenticated = apiKeyAuthenticationProvider.authenticate(authentication);
        assertEquals(authentication, authenticated);
    }

    @Test
    void authenticateTest_noApiKey() {
        ReflectionTestUtils.setField(apiKeyAuthenticationProvider, "apiKeySecret", "apikey");

        when(authentication.getPrincipal()).thenReturn("test");

        assertThrows(AutoHubException.class, () -> apiKeyAuthenticationProvider.authenticate(authentication));
    }

}
