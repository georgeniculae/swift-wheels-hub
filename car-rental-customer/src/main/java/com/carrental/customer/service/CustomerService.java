package com.carrental.customer.service;

import com.carrental.customer.dto.CurrentUserDto;
import com.carrental.customer.mapper.CustomerMapper;
import com.carrental.dto.AuthenticationResponse;
import com.carrental.dto.RegisterRequest;
import com.carrental.dto.UserDto;
import com.carrental.entity.Role;
import com.carrental.entity.User;
import com.carrental.lib.exception.CarRentalNotFoundException;
import com.carrental.lib.exception.CarRentalResponseStatusException;
import com.carrental.lib.repository.UserRepository;
import com.carrental.lib.security.jwt.JwtService;
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

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;

    public AuthenticationResponse registerCustomer(RegisterRequest request) {
        var jwtToken = jwtService.generateToken(saveUser(request));

        return new AuthenticationResponse().token(jwtToken);
    }

    public CurrentUserDto getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = findByUsername(username);

        return customerMapper.mapUserToCurrentUserDto(user);
    }

    public UserDto findUserByUsername(String username) {
        User user = findByUsername(username);

        return customerMapper.mapEntityToDto(user);
    }

    public Long countUsers() {
        return userRepository.count();
    }

    public User saveUser(RegisterRequest request) {
        validateRequest(request);
        User user = new User();

        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setAddress(request.getAddress());
        user.setRole(Role.ROLE_USER);
        user.setDateOfBirth(request.getDateOfBirth());

        return userRepository.saveAndFlush(user);
    }

    public UserDto updateUser(Long id, UserDto userDto) {
        User user = findEntityById(id);

        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        User savedUser = userRepository.saveAndFlush(user);

        return customerMapper.mapEntityToDto(savedUser);
    }

    @Transactional
    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username);
    }

    private User findEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CarRentalNotFoundException("User with id " + id + " doesn't exist"));
    }

    private User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CarRentalNotFoundException("User with username " + username + " doesn't exist"));
    }

    private void validateRequest(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        if (Optional.ofNullable(request.getPassword()).orElseThrow().length() < 8) {
            throw new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "Password too short");
        }

        if (Period.between(Optional.ofNullable(request.getDateOfBirth()).orElseThrow(), LocalDate.now()).getYears() < 18) {
            throw new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "Customer is under 18 years old");
        }
    }

}
