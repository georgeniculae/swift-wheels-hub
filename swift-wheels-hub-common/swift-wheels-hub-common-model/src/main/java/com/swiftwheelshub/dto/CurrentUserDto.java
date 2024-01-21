package com.swiftwheelshub.dto;

import com.swiftwheelshub.entity.Role;
import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Builder
public record CurrentUserDto(Long id,
                             String username,
                             String password,
                             Role role,
                             String firstName,
                             String lastName,
                             String email,
                             Boolean credentialsNonExpired,
                             Boolean accountNonExpired,
                             Boolean accountNonLocked,
                             Collection<? extends GrantedAuthority> authorities) {

}
