package com.carrental.agency.service;

import com.carrental.agency.mapper.EmployeeMapper;
import com.carrental.agency.mapper.EmployeeMapperImpl;
import com.carrental.agency.repository.EmployeeRepository;
import com.carrental.agency.util.AssertionUtils;
import com.carrental.agency.util.TestUtils;
import com.carrental.dto.EmployeeDto;
import com.carrental.entity.Branch;
import com.carrental.entity.Employee;
import com.carrental.lib.exception.CarRentalNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @InjectMocks
    private EmployeeService employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private BranchService branchService;

    @Spy
    private EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @Test
    void findAllEmployeesTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        List<EmployeeDto> employeeDtoList = assertDoesNotThrow(() -> employeeService.findAllEmployees());
        AssertionUtils.assertEmployee(employee, employeeDtoList.get(0));
    }

    @Test
    void findEmployeeByIdTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        EmployeeDto employeeDto = assertDoesNotThrow(() -> employeeService.findEmployeeById(1L));
        AssertionUtils.assertEmployee(employee, employeeDto);
    }

    @Test
    void findEmployeeByIdTest_errorOnFindingById() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        CarRentalNotFoundException carRentalNotFoundException =
                assertThrows(CarRentalNotFoundException.class, () -> employeeService.findEmployeeById(1L));

        assertNotNull(carRentalNotFoundException);
        assertEquals("Employee with id 1 does not exist", carRentalNotFoundException.getMessage());
    }

    @Test
    void saveEmployeeTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenReturn(employee);

        EmployeeDto savedEmployeeDto = assertDoesNotThrow(() -> employeeService.saveEmployee(employeeDto));
        AssertionUtils.assertEmployee(employee, savedEmployeeDto);

        verify(employeeMapper, times(1)).mapEntityToDto(any(Employee.class));
    }

    @Test
    void updateEmployeeTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);
        EmployeeDto employeeDto = TestUtils.getResourceAsJson("/data/EmployeeDto.json", EmployeeDto.class);
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenReturn(employee);

        EmployeeDto updatedEmployeeDto = assertDoesNotThrow(() -> employeeService.updateEmployee(1L, employeeDto));
        AssertionUtils.assertEmployee(employee, updatedEmployeeDto);
    }

    @Test
    void findEmployeesByBranchIdTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findAllEmployeesByBranchId(anyLong())).thenReturn(List.of(employee));

        List<EmployeeDto> employeeDtoList = assertDoesNotThrow(() -> employeeService.findEmployeesByBranchId(1L));
        AssertionUtils.assertEmployee(employee, employeeDtoList.get(0));
    }

    @Test
    void findEmployeeByFilterTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findByFilter(anyString())).thenReturn(Optional.of(employee));

        EmployeeDto employeeDto = assertDoesNotThrow(() -> employeeService.findEmployeeByFilter("Ion"));
        AssertionUtils.assertEmployee(employee, employeeDto);
    }

    @Test
    void findEmployeeByFilterTest_errorOnFindingByFilter() {
        when(employeeRepository.findByFilter(anyString())).thenReturn(Optional.empty());

        CarRentalNotFoundException carRentalNotFoundException =
                assertThrows(CarRentalNotFoundException.class, () -> employeeService.findEmployeeByFilter("Test"));

        assertNotNull(carRentalNotFoundException);
        assertEquals("Employee with filter: Test does not exist", carRentalNotFoundException.getMessage());
    }

}
