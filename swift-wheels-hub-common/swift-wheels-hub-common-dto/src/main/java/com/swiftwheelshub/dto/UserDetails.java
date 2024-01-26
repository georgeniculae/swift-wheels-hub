package com.swiftwheelshub.dto;

import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;

@Builder
public record UserDetails(
        String username,
        String firstName,
        String lastName,
        String email,
        String address,
        LocalDate dateOfBirth,
        Collection<? extends GrantedAuthority> authorities) {
}
