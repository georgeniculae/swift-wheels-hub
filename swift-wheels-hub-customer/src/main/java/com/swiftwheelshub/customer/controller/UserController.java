package com.swiftwheelshub.customer.controller;

import com.swiftwheelshub.customer.service.CustomerService;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserDetails;
import com.swiftwheelshub.dto.UserUpdateRequest;
import com.swiftwheelshub.lib.aspect.LogActivity;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final CustomerService customerService;

    @GetMapping(path = "/current")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<UserDetails> getCurrentUser(HttpServletRequest request) {
        return ResponseEntity.ok(customerService.getCurrentUser(request));
    }

    @GetMapping(path = "/{username}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UserDetails> findUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(customerService.findUserByUsername(username));
    }

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('admin')")
    @LogActivity(
            sentParameters = "registerRequest",
            activityDescription = "User registration"
    )
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody @Valid RegisterRequest registerRequest) {
        return ResponseEntity.ok(customerService.registerCustomer(registerRequest));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @LogActivity(
            sentParameters = "id",
            activityDescription = "User update"
    )
    public ResponseEntity<UserDetails> updateUser(@PathVariable("id") String id,
                                                  @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(customerService.updateUser(id, userUpdateRequest));
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Integer> countUsers() {
        return ResponseEntity.ok(customerService.countUsers());
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @LogActivity(
            sentParameters = "id",
            activityDescription = "User deletion"
    )
    public ResponseEntity<Void> deleteUserByUsername(@PathVariable("id") String id) {
        customerService.deleteUserByUsername(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/sign-out")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        customerService.signOut(request);

        return ResponseEntity.noContent().build();
    }

}
