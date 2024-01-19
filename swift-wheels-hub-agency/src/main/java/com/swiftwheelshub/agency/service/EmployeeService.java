package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.EmployeeMapper;
import com.swiftwheelshub.agency.repository.EmployeeRepository;
import com.swiftwheelshub.dto.EmployeeDto;
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

    public List<EmployeeDto> findAllEmployees() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::mapEntityToDto)
                .toList();
    }

    public void deleteEmployeeById(Long id) {
        employeeRepository.deleteById(id);
    }

    public EmployeeDto findEmployeeById(Long id) {
        Employee employee = findEntityById(id);

        return employeeMapper.mapEntityToDto(employee);
    }

    public Employee findEntityById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Employee with id " + id + " does not exist"));
    }

    public EmployeeDto saveEmployee(EmployeeDto employeeDto) {
        Employee newEmployee = employeeMapper.mapDtoToEntity(employeeDto);

        Long workingBranchId = employeeDto.workingBranchId();
        Branch workingBranch = branchService.findEntityById(workingBranchId);
        newEmployee.setWorkingBranch(workingBranch);
        Employee savedEmployee = saveEntity(newEmployee);

        return employeeMapper.mapEntityToDto(savedEmployee);
    }

    public EmployeeDto updateEmployee(Long id, EmployeeDto updatedEmployeeDto) {
        Employee existingEmployee = findEntityById(id);

        Long workingBranchId = updatedEmployeeDto.workingBranchId();
        Branch workingBranch = branchService.findEntityById(workingBranchId);

        existingEmployee.setFirstName(updatedEmployeeDto.firstName());
        existingEmployee.setLastName(updatedEmployeeDto.lastName());
        existingEmployee.setJobPosition(updatedEmployeeDto.jobPosition());
        existingEmployee.setWorkingBranch(workingBranch);

        Employee savedEmployee = saveEntity(existingEmployee);

        return employeeMapper.mapEntityToDto(savedEmployee);
    }

    public List<EmployeeDto> findEmployeesByBranchId(Long id) {
        return employeeRepository.findAllEmployeesByBranchId(id)
                .stream()
                .map(employeeMapper::mapEntityToDto)
                .toList();
    }

    public EmployeeDto findEmployeeByFilter(String searchString) {
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
