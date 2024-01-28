package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.customer.mapper.UserMapper;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDetails;
import com.swiftwheelshub.dto.UserUpdateRequest;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.NotFoundException;
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
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private static final String ADDRESS = "address";

    private static final String DATE_OF_BIRTH = "dateOfBirth";

    @Value("${keycloak.realm}")
    private String realm;

    private final Keycloak keycloak;

    private final UserMapper userMapper;

    public UserDetails findUserByUsername(String username) {
        UserRepresentation userRepresentation = getUserRepresentation(username);

        return userMapper.mapUserToUserDetails(userRepresentation);
    }

    public UserDetails getCurrentUser(HttpServletRequest request) {
        String username = HttpRequestUtil.extractUsername(request);

        return findUserByUsername(username);
    }

    public Integer countUsers() {
        return getUsersResource().count();
    }

    public RegistrationResponse registerCustomer(RegisterRequest request) {
        validateRequest(request);
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

    public UserDetails updateUser(String id, UserUpdateRequest userUpdateRequest) {
        UserResource userResource = findById(id);

        UserRepresentation userRepresentation = userMapper.mapToUserRepresentation(userUpdateRequest);
        userRepresentation.singleAttribute(ADDRESS, userUpdateRequest.address());
        userRepresentation.singleAttribute(DATE_OF_BIRTH, userUpdateRequest.dateOfBirth().toString());

        try {
            userResource.update(userRepresentation);
        } catch (Exception e) {
            handleRestEasyCall(e);
        }

        return userMapper.mapUserToUserDetails(userRepresentation);
    }

    public void deleteUserByUsername(String id) {
        UserResource userResource = findById(id);

        try {
            userResource.remove();
        } catch (Exception e) {
            handleRestEasyCall(e);
        }
    }

    public void signOut(HttpServletRequest request) {
        String username = HttpRequestUtil.extractUsername(request);
        UserRepresentation userRepresentation = getUserRepresentation(username);

        try {
            findById(userRepresentation.getId()).logout();
        } catch (Exception e) {
            handleRestEasyCall(e);
        }
    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private UserResource findById(String id) {
        return getUsersResource().get(id);
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
        userRepresentation.singleAttribute(DATE_OF_BIRTH, request.dateOfBirth().toString());
        userRepresentation.setEmailVerified(false);
        userRepresentation.setEnabled(true);

        return userRepresentation;
    }

    private void makeEmailVerification(String userId) {
        try {
            findById(userId).sendVerifyEmail();
        } catch (Exception e) {
            handleRestEasyCall(e);
        }
    }

    private RegistrationResponse getRegistrationResponse(UserRepresentation userRepresentation, Response response,
                                                         RegisterRequest request) {
        String createdId = CreatedResponseUtil.getCreatedId(response);
        UserResource userResource = findById(createdId);

        try {
            userResource.resetPassword(createPasswordCredentials(request.password()));
        } catch (Exception e) {
            handleRestEasyCall(e);
        }

        if (request.needsEmailVerification()) {
            makeEmailVerification(getUserId(userRepresentation.getUsername()));
        }

        return userMapper.mapToRegistrationResponse(userRepresentation);
    }

    private UserRepresentation getUserRepresentation(String username) {
        List<UserRepresentation> userRepresentations = getUserRepresentations(username);

        if (userRepresentations.isEmpty()) {
            throw new SwiftWheelsHubNotFoundException("User with username " + username + " doesn't exist");
        }

        return userRepresentations.getFirst();
    }

    private List<UserRepresentation> getUserRepresentations(String username) {
        return getUsersResource().searchByUsername(username, true);
    }

    private String getUserId(String username) {
        return getUserRepresentation(username).getId();
    }

    private void validateRequest(RegisterRequest request) {
        if (Optional.ofNullable(request.password()).orElseThrow().length() < 8) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");
        }

        if (Period.between(Optional.ofNullable(request.dateOfBirth()).orElseThrow(), LocalDate.now()).getYears() < 18) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Customer is under 18 years old");
        }
    }

    private void handleRestEasyCall(Exception e) {
        if (e instanceof NotFoundException) {
            throw new SwiftWheelsHubNotFoundException("User not found");
        }

        throw new SwiftWheelsHubException(e);
    }

}
