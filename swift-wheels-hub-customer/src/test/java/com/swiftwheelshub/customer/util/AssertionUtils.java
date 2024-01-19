package com.swiftwheelshub.customer.util;

import com.swiftwheelshub.dto.CurrentUserDto;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;

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
        assertEquals(user.getUsername(), userDto.username());
        assertEquals(user.getPassword(), userDto.password());
        assertEquals(user.getRole(), userDto.role());
        assertEquals(user.getFirstName(), userDto.firstName());
        assertEquals(user.getLastName(), userDto.lastName());
        assertEquals(user.getEmail(), userDto.email());
    }

    public static void assertUser(RegisterRequest registerRequest, User user) {
        assertEquals(registerRequest.username(), user.getUsername());
        assertEquals(registerRequest.password(), user.getPassword());
        assertEquals(registerRequest.firstName(), user.getFirstName());
        assertEquals(registerRequest.lastName(), user.getLastName());
        assertEquals(registerRequest.email(), user.getEmail());
    }

}
