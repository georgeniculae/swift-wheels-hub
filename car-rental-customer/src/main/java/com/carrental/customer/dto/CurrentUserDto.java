package com.carrental.customer.dto;

import com.carrental.entity.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

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
