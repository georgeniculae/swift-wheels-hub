package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.EmployeeService;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAllEmployees() {
        List<EmployeeResponse> employeeResponses = employeeService.findAllEmployees();

        return ResponseEntity.ok(employeeResponses);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<EmployeeResponse> findEmployeeById(@PathVariable("id") Long id) {
        EmployeeResponse employeeResponse = employeeService.findEmployeeById(id);

        return ResponseEntity.ok(employeeResponse);
    }

    @GetMapping(path = "/branch/{id}")
    public ResponseEntity<List<EmployeeResponse>> findEmployeesByBranchId(@PathVariable("id") Long id) {
        List<EmployeeResponse> employeeResponses = employeeService.findEmployeesByBranchId(id);

        return ResponseEntity.ok(employeeResponses);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countEmployees() {
        Long numberOfEmployees = employeeService.countEmployees();

        return ResponseEntity.ok(numberOfEmployees);
    }

    @PostMapping
    public ResponseEntity<EmployeeResponse> addEmployee(@RequestBody @Valid EmployeeRequest employeeRequest) {
        EmployeeResponse savedEmployeeResponse = employeeService.saveEmployee(employeeRequest);

        return ResponseEntity.ok(savedEmployeeResponse);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable("id") Long id,
                                                           @RequestBody @Valid EmployeeRequest employeeRequest) {
        EmployeeResponse updatedEmployeeResponse = employeeService.updateEmployee(id, employeeRequest);

        return ResponseEntity.ok(updatedEmployeeResponse);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable("id") Long id) {
        employeeService.deleteEmployeeById(id);

        return ResponseEntity.noContent().build();
    }

}
