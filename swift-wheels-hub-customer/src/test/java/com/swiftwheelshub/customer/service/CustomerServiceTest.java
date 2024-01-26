package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.customer.mapper.UserMapper;
import com.swiftwheelshub.customer.mapper.UserMapperImpl;
import com.swiftwheelshub.customer.util.AssertionUtils;
import com.swiftwheelshub.customer.util.TestUtils;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.core.ServerResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private Keycloak keycloak;

    @Mock
    private UsersResource usersResource;

    @Mock
    private UserResource userResource;

    @Mock
    private RealmResource realmResource;

    @Spy
    private UserMapper userMapper = new UserMapperImpl();

    @Test
    @SuppressWarnings("all")
    void registerCustomerTest_success() {
        ReflectionTestUtils.setField(customerService, "realm", "realm");

        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        Headers<Object> headers = new Headers<>();
        headers.put("test", List.of());
        Response response = new ServerResponse(null, 201, headers);

        mockStatic(CreatedResponseUtil.class);
        when(CreatedResponseUtil.getCreatedId(any())).thenReturn("id");
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        when(usersResource.get(anyString())).thenReturn(userResource);
        doNothing().when(userResource).resetPassword(any(CredentialRepresentation.class));

        RegistrationResponse registrationResponse = customerService.registerCustomer(registerRequest);

        AssertionUtils.assertRegistrationResponse(registerRequest, registrationResponse);

        verify(userMapper).mapToRegistrationResponse(any(UserRepresentation.class));
    }

    @Test
    void registerCustomerTest_customerUnderAge() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequestAgeBelow18.json", RegisterRequest.class);

        SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException =
                assertThrows(SwiftWheelsHubResponseStatusException.class, () -> customerService.registerCustomer(registerRequest));

        assertNotNull(swiftWheelsHubResponseStatusException);
        assertThat(swiftWheelsHubResponseStatusException.getMessage()).contains("Customer is under 18 years old");
    }

    @Test
    void registerCustomerTest_passwordTooShort() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequestPasswordTooShort.json", RegisterRequest.class);

        SwiftWheelsHubResponseStatusException swiftWheelsHubResponseStatusException =
                assertThrows(SwiftWheelsHubResponseStatusException.class, () -> customerService.registerCustomer(registerRequest));

        assertNotNull(swiftWheelsHubResponseStatusException);
        assertThat(swiftWheelsHubResponseStatusException.getMessage()).contains("Password too short");
    }

    @Test
    void getCurrentUserTest_success() {

//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        assertDoesNotThrow(() -> customerService.getCurrentUser());
//
//        verify(userMapper).mapUserToUserDetails(any(UserRepresentation.class));
    }

//    @Test
//    void getCurrentUserTest_errorOnFindingByUsername() {
//        Authentication authentication = mock(Authentication.class);
//
//        when(securityContext.getAuthentication()).thenReturn(authentication);
//        SecurityContextHolder.setContext(securityContext);
//
//        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());
//
//        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
//                assertThrows(SwiftWheelsHubNotFoundException.class, () -> customerService.getCurrentUser());
//
//        assertNotNull(swiftWheelsHubNotFoundException);
//        assertEquals("User with username null doesn't exist", swiftWheelsHubNotFoundException.getMessage());
//    }

//    @Test
//    void findUserByUsernameTest_success() {
//        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
//        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
//
//        UserDetails userDetails = assertDoesNotThrow(() -> customerService.findUserByUsername("admin"));
//        AssertionUtils.assertUser(user, userDetails);
//    }

//    @Test
//    void updateUserTest_success() {
//        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
//        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
//        when(passwordEncoder.encode(anyString())).thenReturn("encoded password");
//        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);
//
//        UserDto updatedUserDto = assertDoesNotThrow(() -> customerService.updateUser(1L, userDto));
//
//        assertEquals(user.getFirstName(), updatedUserDto.firstName());
//        assertEquals(user.getLastName(), updatedUserDto.lastName());
//        assertEquals(user.getEmail(), updatedUserDto.email());
//    }

//    @Test
//    void updateUserTest_errorOnFindingById() {
//        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);
//
//        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
//
//        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
//                assertThrows(SwiftWheelsHubNotFoundException.class, () -> customerService.updateUser("1", userDto));
//
//        assertNotNull(swiftWheelsHubNotFoundException);
//        assertEquals("User with id 1 doesn't exist", swiftWheelsHubNotFoundException.getMessage());
//    }

}
