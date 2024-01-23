package com.swiftwheelshub.customer.controller;

import com.swiftwheelshub.customer.service.CustomerService;
import com.swiftwheelshub.customer.service.KeycloakUserService;
import com.swiftwheelshub.dto.CurrentUserDto;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDto;
import com.swiftwheelshub.lib.aspect.LogActivity;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final CustomerService customerService;
    private final KeycloakUserService keycloakUserService;

    @GetMapping(path = "/current")
    public ResponseEntity<CurrentUserDto> getCurrentUser() {
        return ResponseEntity.ok(customerService.getCurrentUser());
    }

    @GetMapping(path = "/keycloak-user/{username}")
    public ResponseEntity<List<UserRepresentation>> getUser(@PathVariable("username") String username) {
        return ResponseEntity.ok(keycloakUserService.getUser(username));
    }

    @GetMapping(path = "/{username}")
    public ResponseEntity<UserDto> findUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(customerService.findUserByUsername(username));
    }

    @PostMapping("/register")
    @LogActivity(
            sentParameters = "registerRequest",
            activityDescription = "User registration"
    )
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(customerService.registerCustomer(registerRequest));
    }

    @PutMapping(path = "/{id}")
    @LogActivity(
            sentParameters = "id",
            activityDescription = "User update"
    )
    public ResponseEntity<UserDto> updateUser(@PathVariable("id") Long id, @RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(customerService.updateUser(id, userDto));
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countUsers() {
        return ResponseEntity.ok(customerService.countUsers());
    }

    @DeleteMapping(path = "/{username}")
    @LogActivity(
            sentParameters = "username",
            activityDescription = "User deletion"
    )
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable("username") String username) {
        customerService.deleteUserByUsername(username);

        return ResponseEntity.noContent().build();
    }

}
