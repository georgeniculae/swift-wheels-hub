package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakUserService {

    private static final String UTC = "UTC";

    private static final String ADDRESS = "address";

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    public List<UserRepresentation> getUser(String username) {
        UsersResource usersResource = getUsersResource();

        return usersResource.searchByUsername(username, true);
    }

    public RegistrationResponse createUser(RegisterRequest request) {
        UserRepresentation userRepresentation = createUserRepresentation(request);

        try (Response response = getUsersResource().create(userRepresentation)) {
            final int statusCode = response.getStatus();

            if (HttpStatus.CREATED.value() == statusCode) {
                return getRegistrationResponse(userRepresentation, response, request);
            }

            throw new SwiftWheelsHubResponseStatusException(
                    HttpStatusCode.valueOf(statusCode),
                    "User could not be created: " + response.getStatusInfo().getReasonPhrase()
            );
        }
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);

        return passwordCredentials;
    }

    private UserRepresentation createUserRepresentation(RegisterRequest request) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(request.username());
        userRepresentation.setFirstName(request.firstName());
        userRepresentation.setLastName(request.lastName());
        userRepresentation.setEmail(request.email());
        userRepresentation.setCredentials(List.of(createPasswordCredentials(request.password())));
        userRepresentation.singleAttribute(ADDRESS, request.address());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(true);

        return userRepresentation;
    }

    private void makeEmailVerification(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();
    }

    private RegistrationResponse getRegistrationResponse(UserRepresentation userRepresentation, Response response,
                                                         RegisterRequest request) {
        UserResource userResource = getUsersResource().get(CreatedResponseUtil.getCreatedId(response));
        userResource.resetPassword(createPasswordCredentials(request.password()));

        if (request.needsEmailVerification()) {
            makeEmailVerification(getUserId(userRepresentation.getUsername()));
        }

        return RegistrationResponse.builder()
                .username(userRepresentation.getUsername())
                .email(userRepresentation.getEmail())
                .firstName(userRepresentation.getFirstName())
                .lastName(userRepresentation.getLastName())
                .registrationDate(getRegistrationDate())
                .build();
    }

    private String getUserId(String username) {
        return getUser(username).getFirst().getId();
    }

    private String getRegistrationDate() {
        return ZonedDateTime.of(LocalDate.now(), LocalTime.now(), ZoneId.of(UTC))
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_DATE_TIME);
    }

}