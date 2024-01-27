package com.swiftwheelshub.customer.util;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDetails;
import com.swiftwheelshub.dto.UserUpdateRequest;
import org.keycloak.representations.idm.UserRepresentation;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertRegistrationResponse(UserRepresentation userRepresentation, RegistrationResponse registrationResponse) {
        assertEquals(userRepresentation.getUsername(), registrationResponse.username());
        assertEquals(userRepresentation.getFirstName(), registrationResponse.firstName());
        assertEquals(userRepresentation.getLastName(), registrationResponse.lastName());
        assertEquals(userRepresentation.getEmail(), registrationResponse.email());
        assertEquals(userRepresentation.getAttributes().get("address").getFirst(), registrationResponse.address());
        assertEquals(userRepresentation.getAttributes().get("dateOfBirth").getFirst(), registrationResponse.dateOfBirth().toString());
    }

    public static void assertRegistrationResponse(RegisterRequest registerRequest, RegistrationResponse registrationResponse) {
        assertEquals(registerRequest.username(), registrationResponse.username());
        assertEquals(registerRequest.firstName(), registrationResponse.firstName());
        assertEquals(registerRequest.lastName(), registrationResponse.lastName());
        assertEquals(registerRequest.email(), registrationResponse.email());
        assertEquals(registerRequest.address(), registrationResponse.address());
        assertEquals(registerRequest.dateOfBirth(), registrationResponse.dateOfBirth());
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

    public static void assertUserDetails(UserUpdateRequest userUpdateRequest, UserDetails userDetails) {
        assertEquals(userUpdateRequest.username(), userDetails.username());
        assertEquals(userUpdateRequest.firstName(), userDetails.firstName());
        assertEquals(userUpdateRequest.lastName(), userDetails.lastName());
        assertEquals(userUpdateRequest.email(), userDetails.email());
        assertEquals(userUpdateRequest.address(), userDetails.address());
        assertEquals(userUpdateRequest.dateOfBirth(), userDetails.dateOfBirth());
    }

}
