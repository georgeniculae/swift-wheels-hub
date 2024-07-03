package com.swiftwheelshub.lib.util;

import com.swiftwheelshub.lib.security.ApiKeyAuthenticationToken;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;

@UtilityClass
public class AuthenticationUtil {

    public static ApiKeyAuthenticationToken getAuthentication() {
        return (ApiKeyAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
    }

}
