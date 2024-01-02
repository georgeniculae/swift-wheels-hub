package com.carrental.agency.service;

import com.carrental.agency.mapper.EmployeeMapper;
import com.carrental.agency.repository.EmployeeRepository;
import com.carrental.dto.EmployeeDto;
import com.carrental.entity.Branch;
import com.carrental.entity.Employee;
import com.carrental.exception.CarRentalNotFoundException;
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
                .orElseThrow(() -> new CarRentalNotFoundException("Employee with id " + id + " does not exist"));
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
                .orElseThrow(() -> new CarRentalNotFoundException("Employee with filter: " + searchString + " does not exist"));
    }

    public Long countEmployees() {
        return employeeRepository.count();
    }

    private Employee saveEntity(Employee newEmployee) {
        return employeeRepository.saveAndFlush(newEmployee);
    }

}
