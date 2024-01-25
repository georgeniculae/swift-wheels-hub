package com.swiftwheelshub.dto;

import com.swiftwheelshub.entity.Role;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;

@Builder
public record UserDetails(Long id,
                          String username,
                          Role role,
                          String firstName,
                          String lastName,
                          String email,
                          String address,
                          LocalDate dateOfBirth,
                          Collection<? extends GrantedAuthority> authorities) {
}
