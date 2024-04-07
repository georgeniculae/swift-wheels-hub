package com.swiftwheelshub.customer.controller;

import com.swiftwheelshub.customer.service.CustomerService;
import com.swiftwheelshub.dto.RegisterRequest;
import com.swiftwheelshub.dto.RegistrationResponse;
import com.swiftwheelshub.dto.UserInfo;
import com.swiftwheelshub.dto.UserUpdateRequest;
import com.swiftwheelshub.lib.aspect.LogActivity;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
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
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping(path = "/infos")
    @Secured("admin")
    public ResponseEntity<List<UserInfo>> findAllUsers() {
        List<UserInfo> allCustomers = customerService.findAllUsers();

        return ResponseEntity.ok(allCustomers);
    }

    @GetMapping(path = "/current")
    @Secured("user")
    public ResponseEntity<UserInfo> getCurrentUser(HttpServletRequest request) {
        return ResponseEntity.ok(customerService.getCurrentUser(request));
    }

    @GetMapping(path = "/{username}")
    @Secured("admin")
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
    @Secured("admin")
    @LogActivity(
            sentParameters = "id",
            activityDescription = "User update"
    )
    public ResponseEntity<UserInfo> updateUser(@PathVariable("id") String id,
                                               @RequestBody @Validated UserUpdateRequest userUpdateRequest) {
        return ResponseEntity.ok(customerService.updateUser(id, userUpdateRequest));
    }

    @GetMapping(path = "/count")
    @Secured("admin")
    public ResponseEntity<Integer> countUsers() {
        return ResponseEntity.ok(customerService.countUsers());
    }

    @DeleteMapping(path = "/{username}")
    @Secured("admin")
    @LogActivity(
            sentParameters = "username",
            activityDescription = "User deletion"
    )
    public ResponseEntity<Void> deleteUserByUsername(HttpServletRequest request,
                                                     @PathVariable("username") String username) {
        customerService.deleteUserByUsername(request);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/current")
    @Secured("admin")
    @LogActivity(
            activityDescription = "Current user deletion"
    )
    public ResponseEntity<Void> deleteCurrentUser(HttpServletRequest request) {
        customerService.deleteUserByUsername(request);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/sign-out")
    @Secured("user")
    public ResponseEntity<Void> signOut(HttpServletRequest request) {
        customerService.signOut(request);

        return ResponseEntity.noContent().build();
    }

}
