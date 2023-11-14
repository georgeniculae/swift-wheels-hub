package service;

import com.carrental.cloudgateway.mapper.UserMapper;
import com.carrental.cloudgateway.mapper.UserMapperImpl;
import com.carrental.cloudgateway.model.User;
import com.carrental.cloudgateway.repository.UserRepository;
import com.carrental.cloudgateway.service.UserService;
import com.carrental.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import util.TestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDtoServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    void saveUserTest_success() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        when(r2dbcEntityTemplate.insert(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.saveUser(userDto))
                .expectNext(user)
                .verifyComplete();

        verify(userMapper, times(1)).mapUserDtoToUser(any(UserDto.class));
    }

    @Test
    void saveUserTest_throwExceptionOnSave() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(r2dbcEntityTemplate.insert(any(User.class))).thenReturn(Mono.error(new Exception()));

        StepVerifier.create(userService.saveUser(userDto))
                .expectComplete()
                .verify();

        verify(userMapper, times(1)).mapUserDtoToUser(any(UserDto.class));
    }

    @Test
    void updateUserTest_success() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        when(r2dbcEntityTemplate.selectOne(any(Query.class), eq(User.class))).thenReturn(Mono.just(user));
        when(r2dbcEntityTemplate.update(any(User.class))).thenReturn(Mono.just(user));

        StepVerifier.create(userService.updateUser(userDto))
                .expectNext(user)
                .verifyComplete();
    }

}
