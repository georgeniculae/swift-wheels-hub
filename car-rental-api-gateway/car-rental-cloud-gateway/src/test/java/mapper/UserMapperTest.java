package mapper;

import com.carrental.cloudgateway.mapper.UserMapper;
import com.carrental.cloudgateway.mapper.UserMapperImpl;
import com.carrental.cloudgateway.model.User;
import com.carrental.dto.UserDto;
import com.carrental.entity.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import util.AssertionUtils;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void mapUserDtoToUserTest() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        User user = assertDoesNotThrow(() -> userMapper.mapUserDtoToUser(userDto));
        AssertionUtils.assertUser(userDto, user);
    }

    @Test
    void mapToUserRoleEnumTest_admin() {
        Role role = assertDoesNotThrow(() -> userMapper.mapToUserRoleEnum(UserDto.RoleEnum.ADMIN));

        assertEquals(Role.ROLE_ADMIN, role);
    }

    @Test
    void mapToUserRoleEnumTest_user() {
        Role role = assertDoesNotThrow(() -> userMapper.mapToUserRoleEnum(UserDto.RoleEnum.USER));

        assertEquals(Role.ROLE_USER, role);
    }

    @Test
    void mapToUserRoleEnumTest_support() {
        Role role = assertDoesNotThrow(() -> userMapper.mapToUserRoleEnum(UserDto.RoleEnum.SUPPORT));

        assertEquals(Role.ROLE_SUPPORT, role);
    }

}
