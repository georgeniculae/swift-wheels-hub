package com.swiftwheelshub.customer.mapper;

import com.swiftwheelshub.customer.util.AssertionUtils;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.UserDetails;
import com.swiftwheelshub.dto.UserUpdateRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void mapToUserRepresentationTest_success() {
        UserUpdateRequest userUpdateRequest =
                TestUtils.getResourceAsJson("/data/UserUpdateRequest.json", UserUpdateRequest.class);

        UserRepresentation userRepresentation = userMapper.mapToUserRepresentation(userUpdateRequest);

        AssertionUtils.assertUserRepresentation(userUpdateRequest, userRepresentation);
    }

    @Test
    void mapUserToUserDetailsTest_success() {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue("password");

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername("username");
        userRepresentation.setFirstName("Ion");
        userRepresentation.setLastName("Popescu");
        userRepresentation.setEmail("ionpopescu@email.com");
        userRepresentation.setCredentials(List.of(passwordCredentials));
        userRepresentation.singleAttribute("address", "Ploiesti");
        userRepresentation.singleAttribute("dateOfBirth", "1980-03-05");

        UserDetails userDetails = userMapper.mapUserToUserDetails(userRepresentation);

        AssertionUtils.assertUserDetails(userRepresentation, userDetails);
    }

}
