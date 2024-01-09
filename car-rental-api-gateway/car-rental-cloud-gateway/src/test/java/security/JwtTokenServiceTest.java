package security;

import com.carrental.cloudgateway.model.User;
import com.carrental.cloudgateway.service.JwtTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import util.TestUtils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class JwtTokenServiceTest {

    @InjectMocks
    private JwtTokenService jwtTokenService;

    @Test
    void generateTokenTest_success() {
        ReflectionTestUtils.setField(jwtTokenService, "signingKey", "asdffgdgftyfhfjhjgjhghjghjjhfhjfhfjhfjfh776986hgh");
        ReflectionTestUtils.setField(jwtTokenService, "expiration", 30L);

        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        assertDoesNotThrow(() -> jwtTokenService.generateToken(user));
    }

}
