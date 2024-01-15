package com.carrental.customer.service;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    public List<UserRepresentation> getUser(String username) {
        UsersResource usersResource = getUsers();

        return usersResource.searchByUsername(username, true);
    }

    public int createAccount(final String username, final String password) {
        CredentialRepresentation passwordCredentials = createPasswordCredentials(password);
        UserRepresentation user = createUserRepresentation(username, passwordCredentials);

        Response response = getUsers().create(user);
        final int status = response.getStatus();

        if (status != HttpStatus.CREATED.value()) {
            return status;
        }

        String createdId = getCreatedId(response);
        CredentialRepresentation newPasswordCredentials = createPasswordCredentials(password);

        UserResource userResource = getUsers().get(createdId);
        userResource.resetPassword(newPasswordCredentials);

        return HttpStatus.CREATED.value();
    }

    private UserRepresentation createUserRepresentation(String username, CredentialRepresentation passwordCredentials) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setFirstName("First Name");
        user.setLastName("Last Name");
        user.singleAttribute("customAttribute", "customAttribute");
        user.setCredentials(List.of(passwordCredentials));

        return user;
    }

    private UsersResource getUsers() {
        return keycloak.realm(realm).users();
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);

        return passwordCredentials;
    }

    public String getCreatedId(Response response) {
        URI location = response.getLocation();

        if (!response.getStatusInfo().equals(Response.Status.CREATED)) {
            Response.StatusType statusInfo = response.getStatusInfo();
            throw new RuntimeException("Create method returned status " +
                    statusInfo.getReasonPhrase() + " (Code: " + statusInfo.getStatusCode() + "); expected status: Created (201)");
        }

        if (location == null) {
            return null;
        }

        String path = location.getPath();

        return path.substring(path.lastIndexOf('/') + 1);
    }

}
