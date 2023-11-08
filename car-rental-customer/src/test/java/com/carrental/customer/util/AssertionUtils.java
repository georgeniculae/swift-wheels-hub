package com.carrental.customer.util;

import com.carrental.customer.dto.CurrentUserDto;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import com.carrental.entity.Role;
import com.carrental.entity.User;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCurrentUser(User user, CurrentUserDto currentUserDto) {
        assertEquals(user.getUsername(), currentUserDto.username());
        assertEquals(user.getPassword(), currentUserDto.password());
        assertEquals(user.getRole(), currentUserDto.role());
        assertEquals(user.getFirstName(), currentUserDto.firstName());
        assertEquals(user.getLastName(), currentUserDto.lastName());
        assertEquals(user.getEmail(), currentUserDto.email());
        assertEquals(user.isAccountNonExpired(), currentUserDto.accountNonExpired());
        assertEquals(user.isAccountNonExpired(), currentUserDto.accountNonExpired());
        assertEquals(user.isCredentialsNonExpired(), currentUserDto.credentialsNonExpired());
        assertEquals(user.getAuthorities(), currentUserDto.authorities());
    }

    public static void assertUser(User user, UserDto userDto) {
        assertEquals(user.getUsername(), userDto.getUsername());
        assertEquals(user.getPassword(), userDto.getPassword());
        assertRole(user.getRole(), Objects.requireNonNull(userDto.getRole()));
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    public static void assertUser(RegisterRequest registerRequest, User user) {
        assertEquals(registerRequest.getUsername(), user.getUsername());
        assertEquals(registerRequest.getPassword(), user.getPassword());
        assertEquals(registerRequest.getFirstName(), user.getFirstName());
        assertEquals(registerRequest.getLastName(), user.getLastName());
        assertEquals(registerRequest.getEmail(), user.getEmail());
    }

    private static void assertRole(Role role, UserDto.RoleEnum roleEnum) {
        assertEquals(role.getName(), roleEnum.getValue());
    }

}
