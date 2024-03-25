package com.swiftwheelshub.customer.controller;

import com.swiftwheelshub.customer.service.CustomerService;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.dto.UserUpdateRequest;
import com.swiftwheelshub.lib.aspect.LogActivity;
import com.swiftwheelshub.lib.util.HttpRequestUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
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
    public ResponseEntity<UserInfo> getCurrentUser(HttpServletRequest request) {
        return ResponseEntity.ok(customerService.getCurrentUser(request));
    }

    @GetMapping(path = "/{username}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<UserInfo> findUserByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(customerService.findUserByUsername(username));
    }

    @PostMapping("/register")
    @LogActivity(
            sentParameters = "registerRequest",
            activityDescription = "User registration"
    )
    public ResponseEntity<RegistrationResponse> registerUser(@RequestBody @Validated RegisterRequest registerRequest) {
        return ResponseEntity.ok(customerService.registerCustomer(registerRequest));
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    @LogActivity(
            sentParameters = "id",
            activityDescription = "User update"
    )
    public ResponseEntity<UserInfo> updateUser(@PathVariable("id") String id,
                                               @RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(customerService.updateUser(id, userUpdateRequest));
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Integer> countUsers() {
        return ResponseEntity.ok(customerService.countUsers());
    }

    @DeleteMapping(path = "/{username}")
    @PreAuthorize("hasAuthority('admin')")
    @LogActivity(
            sentParameters = "username",
            activityDescription = "User deletion"
    )
    public ResponseEntity<Void> deleteUserByUsername(HttpServletRequest request,
                                                     @PathVariable("username") String username) {
        customerService.deleteUserByUsername(request, username);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/current")
    @PreAuthorize("hasAuthority('admin')")
    @LogActivity(
            activityDescription = "Current user deletion"
    )
    public ResponseEntity<Void> deleteCurrentUser(HttpServletRequest request) {
        customerService.deleteUserByUsername(request, HttpRequestUtil.extractUsername(request));

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/sign-out")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        customerService.signOut(request);

        return ResponseEntity.noContent().build();
    }

}
