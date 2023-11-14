package com.carrental.customer.service;

import com.carrental.customer.mapper.CustomerMapper;
import com.carrental.customer.mapper.CustomerMapperImpl;
import com.carrental.customer.util.AssertionUtils;
import com.carrental.customer.util.TestUtils;
import com.carrental.dto.AuthenticationResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import com.carrental.entity.User;
import com.carrental.lib.exception.CarRentalNotFoundException;
import com.carrental.lib.exception.CarRentalResponseStatusException;
import com.carrental.lib.repository.UserRepository;
import com.carrental.lib.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Spy
    private CustomerMapper customerMapper = new CustomerMapperImpl();

    @Captor
    private ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

    @Test
    void registerCustomerTest_success() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
        String token = "token";
        String password = "$2a$10$hadYmhDPuigFKchXrkmmUe6i1L8B50Be.ggbdVuszCbYu7yg14Lqa";

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn(password);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any())).thenReturn(token);

        AuthenticationResponse authenticationResponse =
                assertDoesNotThrow(() -> customerService.registerCustomer(registerRequest));

        assertEquals(token, authenticationResponse.token());

        verify(passwordEncoder).encode(any());
        verify(userRepository).saveAndFlush(argumentCaptor.capture());

        AssertionUtils.assertUser(registerRequest, argumentCaptor.getValue());
    }

    @Test
    void saveUserTest_success() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        User savedUser = assertDoesNotThrow(() -> customerService.saveUser(registerRequest));
        AssertionUtils.assertUser(registerRequest, savedUser);
    }

    @Test
    void saveUserTest_existingUsername() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequest.json", RegisterRequest.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        CarRentalResponseStatusException carRentalResponseStatusException =
                assertThrows(CarRentalResponseStatusException.class, () -> customerService.saveUser(registerRequest));

        assertNotNull(carRentalResponseStatusException);
        assertThat(carRentalResponseStatusException.getMessage()).contains("Username already exists");
    }

    @Test
    void saveUserTest_customerUnderAge() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequestAgeBelow18.json", RegisterRequest.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        CarRentalResponseStatusException carRentalResponseStatusException =
                assertThrows(CarRentalResponseStatusException.class, () -> customerService.saveUser(registerRequest));

        assertNotNull(carRentalResponseStatusException);
        assertThat(carRentalResponseStatusException.getMessage()).contains("Customer is under 18 years old");
    }

    @Test
    void saveUserTest_passwordTooShort() {
        RegisterRequest registerRequest =
                TestUtils.getResourceAsJson("/data/RegisterRequestPasswordTooShort.json", RegisterRequest.class);

        when(userRepository.existsByUsername(anyString())).thenReturn(false);

        CarRentalResponseStatusException carRentalResponseStatusException =
                assertThrows(CarRentalResponseStatusException.class, () -> customerService.saveUser(registerRequest));

        assertNotNull(carRentalResponseStatusException);
        assertThat(carRentalResponseStatusException.getMessage()).contains("Password too short");
    }

    @Test
    void getCurrentUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);

        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(null)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> customerService.getCurrentUser());

        verify(customerMapper, times(1)).mapUserToCurrentUserDto(any(User.class));
    }

    @Test
    void getCurrentUserTest_errorOnFindingByUsername() {
        Authentication authentication = mock(Authentication.class);

        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(null)).thenReturn(Optional.empty());

        CarRentalNotFoundException carRentalNotFoundException = assertThrows(CarRentalNotFoundException.class, () -> customerService.getCurrentUser());

        assertNotNull(carRentalNotFoundException);
        assertEquals("User with username null doesn't exist", carRentalNotFoundException.getMessage());
    }

    @Test
    void findUserByUsernameTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserDto userDto = assertDoesNotThrow(() -> customerService.findUserByUsername("admin"));
        AssertionUtils.assertUser(user, userDto);
    }

    @Test
    void updateUserTest_success() {
        User user = TestUtils.getResourceAsJson("/data/User.json", User.class);
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded password");
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(user);

        UserDto updatedUserDto = assertDoesNotThrow(() -> customerService.updateUser(1L, userDto));

        assertEquals(user.getPassword(), updatedUserDto.password());
        assertEquals(user.getFirstName(), updatedUserDto.firstName());
        assertEquals(user.getLastName(), updatedUserDto.lastName());
        assertEquals(user.getEmail(), updatedUserDto.email());
    }

    @Test
    void updateUserTest_errorOnFindingById() {
        UserDto userDto = TestUtils.getResourceAsJson("/data/UserDto.json", UserDto.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        CarRentalNotFoundException carRentalNotFoundException =
                assertThrows(CarRentalNotFoundException.class, () -> customerService.updateUser(1L, userDto));

        assertNotNull(carRentalNotFoundException);
        assertEquals("User with id 1 doesn't exist", carRentalNotFoundException.getMessage());
    }

}
