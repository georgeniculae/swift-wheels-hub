package com.swiftwheelshub.customer.util;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDetails;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCurrentUser(User user, UserDetails userDetails) {
        assertEquals(user.getUsername(), userDetails.username());
        assertEquals(user.getRole(), userDetails.role());
        assertEquals(user.getFirstName(), userDetails.firstName());
        assertEquals(user.getLastName(), userDetails.lastName());
        assertEquals(user.getEmail(), userDetails.email());
        assertEquals(user.getAuthorities(), userDetails.authorities());
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

    public static void assertRegistrationResponse(RegisterRequest registerRequest, RegistrationResponse registrationResponse) {
        assertEquals(registerRequest.username(), registrationResponse.username());
        assertEquals(registerRequest.firstName(), registrationResponse.firstName());
        assertEquals(registerRequest.lastName(), registrationResponse.lastName());
        assertEquals(registerRequest.email(), registrationResponse.email());
    }
}
