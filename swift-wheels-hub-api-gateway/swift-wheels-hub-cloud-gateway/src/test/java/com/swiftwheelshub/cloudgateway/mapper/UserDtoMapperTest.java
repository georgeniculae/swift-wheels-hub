package com.swiftwheelshub.cloudgateway.mapper;

import com.swiftwheelshub.cloudgateway.model.User;
import com.swiftwheelshub.cloudgateway.util.AssertionUtils;
import com.swiftwheelshub.cloudgateway.util.TestUtils;
import com.swiftwheelshub.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class UserDtoMapperTest {

    @InjectMocks
    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void mapUserDtoToUserTest() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        User user = assertDoesNotThrow(() -> userMapper.mapUserDtoToUser(userDto));
        AssertionUtils.assertUser(userDto, user);
    }

}
