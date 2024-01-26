package com.swiftwheelshub.customer.mapper;

import com.swiftwheelshub.customer.util.AssertionUtils;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void mapUserToCurrentUserDtoTest_success() {
        UserUpdateRequest userUpdateRequest =
                TestUtils.getResourceAsJson("/data/UserUpdateRequest.json", UserUpdateRequest.class);

        UserRepresentation userRepresentation = userMapper.mapToUserRepresentation(userUpdateRequest);

        AssertionUtils.assertUserRepresentation(userUpdateRequest, userRepresentation);
    }

}
