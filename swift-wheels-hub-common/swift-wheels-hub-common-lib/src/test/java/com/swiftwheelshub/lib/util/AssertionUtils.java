package com.swiftwheelshub.lib.util;

import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertUser(User user, UserDto userDto) {
        assertEquals(user.getUsername(), userDto.username());
        assertEquals(user.getPassword(), userDto.password());
        assertEquals(user.getFirstName(), userDto.firstName());
        assertEquals(user.getLastName(), userDto.lastName());
        assertEquals(user.getEmail(), userDto.email());
    }

}
