package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.EmployeeMapper;
import com.swiftwheelshub.agency.repository.EmployeeRepository;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Employee;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BranchService branchService;
    private final EmployeeMapper employeeMapper;

    public List<EmployeeResponse> findAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::mapEntityToDto)
                .toList();
    }

    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeResponse findEmployeeById(Long id) {
        Employee employee = findEntityById(id);

        return employeeMapper.mapEntityToDto(employee);
    }

    public Employee findEntityById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Employee with id " + id + " does not exist"));
    }

    public EmployeeResponse saveEmployee(EmployeeRequest employeeRequest) {
        Employee newEmployee = employeeMapper.mapDtoToEntity(employeeRequest);

        Long workingBranchId = employeeRequest.workingBranchId();
        Branch workingBranch = branchService.findEntityById(workingBranchId);
        newEmployee.setWorkingBranch(workingBranch);
        Employee savedEmployee = saveEntity(newEmployee);

        return employeeMapper.mapEntityToDto(savedEmployee);
    }

    public EmployeeResponse updateEmployee(Long id, EmployeeRequest updatedEmployeeRequest) {
        Employee existingEmployee = findEntityById(id);

        Long workingBranchId = updatedEmployeeRequest.workingBranchId();
        Branch workingBranch = branchService.findEntityById(workingBranchId);

        existingEmployee.setFirstName(updatedEmployeeRequest.firstName());
        existingEmployee.setLastName(updatedEmployeeRequest.lastName());
        existingEmployee.setJobPosition(updatedEmployeeRequest.jobPosition());
        existingEmployee.setWorkingBranch(workingBranch);

        Employee savedEmployee = saveEntity(existingEmployee);

        return employeeMapper.mapEntityToDto(savedEmployee);
    }

    public List<EmployeeResponse> findEmployeesByBranchId(Long id) {
        return employeeRepository.findAllEmployeesByBranchId(id)
                .stream()
                .map(employeeMapper::mapEntityToDto)
                .toList();
    }

    public EmployeeResponse findEmployeeByFilter(String searchString) {
        return employeeRepository.findByFilter(searchString)
                .map(employeeMapper::mapEntityToDto)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Employee with filter: " + searchString + " does not exist"));
    }

    public Long countEmployees() {
        return employeeRepository.count();
    }

    private Employee saveEntity(Employee newEmployee) {
        return employeeRepository.saveAndFlush(newEmployee);
    }

}
