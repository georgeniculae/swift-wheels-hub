package com.swiftwheelshub.customer.mapper;

import com.swiftwheelshub.customer.util.AssertionUtils;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.CurrentUserDetails;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void mapUserToCurrentUserDtoTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        CurrentUserDetails currentUserDetails = Assertions.assertDoesNotThrow(() -> userMapper.mapUserToCurrentUserDto(user));

        AssertionUtils.assertCurrentUser(user, currentUserDetails);
    }

    @Test
    void mapUserToCurrentUserDtoTest_null() {
        CurrentUserDetails currentUserDetails = Assertions.assertDoesNotThrow(() -> userMapper.mapUserToCurrentUserDto(null));

        assertNull(currentUserDetails);
    }

    @Test
    void mapEntityToDtoTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        UserDto userDto = Assertions.assertDoesNotThrow(() -> userMapper.mapEntityToDto(user));

        AssertionUtils.assertUser(user, userDto);
    }

    @Test
    void mapEntityToDtoTest_null() {
        UserDto userDto = Assertions.assertDoesNotThrow(() -> userMapper.mapEntityToDto(null));

        assertNull(userDto);
    }

}
