package util;

import com.carrental.cloudgateway.model.User;
import com.carrental.dto.UserDto;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertUser(UserDto userDto, User user) {
        assertEquals(userDto.getUsername(), user.getUsername());
        assertEquals(userDto.getPassword(), user.getPassword());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getFirstName(), user.getFirstName());
        assertEquals(userDto.getLastName(), user.getLastName());
        assertEquals(Objects.requireNonNull(userDto.getRole()).getValue(), user.getRole().getName());
    }

}
