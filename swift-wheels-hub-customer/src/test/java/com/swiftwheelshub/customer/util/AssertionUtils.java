package com.swiftwheelshub.customer.util;

import com.swiftwheelshub.dto.CurrentUserDetails;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCurrentUser(User user, CurrentUserDetails currentUserDetails) {
        assertEquals(user.getUsername(), currentUserDetails.username());
        assertEquals(user.getPassword(), currentUserDetails.password());
        assertEquals(user.getRole(), currentUserDetails.role());
        assertEquals(user.getFirstName(), currentUserDetails.firstName());
        assertEquals(user.getLastName(), currentUserDetails.lastName());
        assertEquals(user.getEmail(), currentUserDetails.email());
        assertEquals(user.isAccountNonExpired(), currentUserDetails.accountNonExpired());
        assertEquals(user.isAccountNonExpired(), currentUserDetails.accountNonExpired());
        assertEquals(user.isCredentialsNonExpired(), currentUserDetails.credentialsNonExpired());
        assertEquals(user.getAuthorities(), currentUserDetails.authorities());
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
