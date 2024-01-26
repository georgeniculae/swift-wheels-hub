package com.swiftwheelshub.customer.util;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDetails;
import com.swiftwheelshub.dto.UserUpdateRequest;
import com.swiftwheelshub.entity.User;
import org.keycloak.representations.idm.UserRepresentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertUser(RegisterRequest registerRequest, User user) {
        assertEquals(registerRequest.username(), user.getUsername());
        assertEquals(registerRequest.password(), user.getPassword());
        assertEquals(registerRequest.firstName(), user.getFirstName());
        assertEquals(registerRequest.lastName(), user.getLastName());
        assertEquals(registerRequest.email(), user.getEmail());
    }

    public static void assertRegistrationResponse(UserRepresentation userRepresentation, RegistrationResponse registrationResponse) {
        assertEquals(userRepresentation.getUsername(), registrationResponse.username());
        assertEquals(userRepresentation.getFirstName(), registrationResponse.firstName());
        assertEquals(userRepresentation.getLastName(), registrationResponse.lastName());
        assertEquals(userRepresentation.getEmail(), registrationResponse.email());
        assertEquals(userRepresentation.getAttributes().get("address").getFirst(), registrationResponse.address());
        assertEquals(userRepresentation.getAttributes().get("dateOfBirth").getFirst(), registrationResponse.dateOfBirth().toString());
    }

    public static void assertUserRepresentation(UserUpdateRequest userUpdateRequest, UserRepresentation userRepresentation) {
        assertEquals(userUpdateRequest.username(), userRepresentation.getUsername());
        assertEquals(userUpdateRequest.firstName(), userRepresentation.getFirstName());
        assertEquals(userUpdateRequest.lastName(), userRepresentation.getLastName());
        assertEquals(userUpdateRequest.email(), userRepresentation.getEmail());
    }

    public static void assertUserDetails(UserRepresentation userRepresentation, UserDetails userDetails) {
        assertEquals(userRepresentation.getUsername(), userDetails.username());
        assertEquals(userRepresentation.getFirstName(), userDetails.firstName());
        assertEquals(userRepresentation.getLastName(), userDetails.lastName());
        assertEquals(userRepresentation.getEmail(), userDetails.email());
        assertEquals(userRepresentation.getAttributes().get("address").getFirst(), userDetails.address());
        assertEquals(userRepresentation.getAttributes().get("dateOfBirth").getFirst(), userDetails.dateOfBirth().toString());
    }

}
