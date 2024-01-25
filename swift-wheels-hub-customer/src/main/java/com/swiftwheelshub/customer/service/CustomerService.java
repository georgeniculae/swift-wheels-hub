package com.swiftwheelshub.customer.service;

import com.swiftwheelshub.customer.mapper.UserMapper;
import com.swiftwheelshub.dto.CurrentUserDetails;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.entity.Role;
import com.swiftwheelshub.entity.User;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import com.swiftwheelshub.customer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final KeycloakUserService keycloakUserService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public RegistrationResponse registerCustomer(RegisterRequest request) {
        validateRequest(request);

        return keycloakUserService.createUser(request);
    }

    public CurrentUserDetails getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = findByUsername(username);

        return userMapper.mapUserToCurrentUserDto(user);
    }

    public UserDto findUserByUsername(String username) {
        User user = findByUsername(username);

        return userMapper.mapEntityToDto(user);
    }

    public Long countUsers() {
        return userRepository.count();
    }

    public User saveUser(RegisterRequest request) {

        User user = new User();

        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setEmail(request.email());
        user.setAddress(request.address());
        user.setRole(Role.ROLE_USER);
        user.setDateOfBirth(request.dateOfBirth());

        return userRepository.saveAndFlush(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = findEntityById(id);

        user.setPassword(passwordEncoder.encode(userDto.password()));
        user.setFirstName(userDto.firstName());
        user.setLastName(userDto.lastName());
        user.setEmail(userDto.email());

        User savedUser = userRepository.saveAndFlush(user);

        return userMapper.mapEntityToDto(savedUser);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    private User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("User with id " + id + " doesn't exist"));
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("User with username " + username + " doesn't exist"));
    }

    private void validateRequest(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (Optional.ofNullable(request.password()).orElseThrow().length() < 8) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");
        }

        if (Period.between(Optional.ofNullable(request.dateOfBirth()).orElseThrow(), LocalDate.now()).getYears() < 18) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Customer is under 18 years old");
        }
    }

}
