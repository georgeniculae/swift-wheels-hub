package com.carrental.agency.controller;

import com.carrental.agency.service.EmployeeService;
import com.carrental.dto.EmployeeDto;
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
    public ResponseEntity<List<EmployeeDto>> findAllEmployees() {
        List<EmployeeDto> employeeDtoList = employeeService.findAllEmployees();

        return ResponseEntity.ok(employeeDtoList);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<EmployeeDto> findEmployeeById(@PathVariable("id") Long id) {
        EmployeeDto employeeDto = employeeService.findEmployeeById(id);

        return ResponseEntity.ok(employeeDto);
    }

    @GetMapping(path = "/branch/{id}")
    public ResponseEntity<List<EmployeeDto>> findEmployeesByBranchId(@PathVariable("id") Long id) {
        List<EmployeeDto> employeeDtoList = employeeService.findEmployeesByBranchId(id);

        return ResponseEntity.ok(employeeDtoList);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countEmployees() {
        Long numberOfEmployees = employeeService.countEmployees();

        return ResponseEntity.ok(numberOfEmployees);
    }

    @PostMapping
    public ResponseEntity<EmployeeDto> addEmployee(@RequestBody @Valid EmployeeDto employeeDto) {
        EmployeeDto savedEmployeeDto = employeeService.saveEmployee(employeeDto);

        return ResponseEntity.ok(savedEmployeeDto);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable("id") Long id,
                                                      @RequestBody @Valid EmployeeDto employeeDto) {
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(id, employeeDto);

        return ResponseEntity.ok(updatedEmployeeDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteEmployeeById(@PathVariable("id") Long id) {
        employeeService.deleteEmployeeById(id);

        return ResponseEntity.noContent().build();
    }

}
