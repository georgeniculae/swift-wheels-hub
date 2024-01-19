package com.swiftwheelshub.cloudgateway.util;

import com.swiftwheelshub.cloudgateway.model.User;
import com.swiftwheelshub.dto.UserDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertUser(UserDto userDto, User user) {
        assertEquals(userDto.username(), user.getUsername());
        assertEquals(userDto.password(), user.getPassword());
        assertEquals(userDto.email(), user.getEmail());
        assertEquals(userDto.firstName(), user.getFirstName());
        assertEquals(userDto.lastName(), user.getLastName());
        assertEquals(Objects.requireNonNull(userDto.role()), user.getRole());
    }

}
