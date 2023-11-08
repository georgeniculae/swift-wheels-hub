package com.carrental.lib.service;

import com.carrental.dto.UserDto;
import com.carrental.entity.User;
import com.carrental.lib.mapper.UserMapper;
import com.carrental.lib.mapper.UserMapperImpl;
import com.carrental.lib.repository.UserRepository;
import com.carrental.lib.util.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    void saveUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        assertDoesNotThrow(() -> userService.saveUser(userDto));

        verify(userMapper).mapDtoToEntity(any(UserDto.class));
    }

    @Test
    void findByUsernameTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        User foundUser = assertDoesNotThrow(() -> userService.findByUsername(user.getUsername()));
        assertThat(user).usingRecursiveAssertion().isEqualTo(foundUser);
    }

    @Test
    void findByUsernameTest_errorOnFindingByUsername() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        IllegalArgumentException illegalArgumentException = assertThrows(IllegalArgumentException.class, () -> userService.findByUsername(""));

        assertNotNull(illegalArgumentException);
        assertEquals("Invalid username or password", illegalArgumentException.getMessage());
    }

}
