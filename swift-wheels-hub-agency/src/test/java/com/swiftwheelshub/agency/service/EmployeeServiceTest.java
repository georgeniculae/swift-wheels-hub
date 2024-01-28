package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.EmployeeMapper;
import com.swiftwheelshub.agency.mapper.EmployeeMapperImpl;
import com.swiftwheelshub.agency.repository.EmployeeRepository;
import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Employee;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
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

        List<EmployeeResponse> employeeResponses = assertDoesNotThrow(() -> employeeService.findAllEmployees());
        AssertionUtils.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

    @Test
    void findEmployeeByIdTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = assertDoesNotThrow(() -> employeeService.findEmployeeById(1L));
        AssertionUtils.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void findEmployeeByIdTest_errorOnFindingById() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> employeeService.findEmployeeById(1L));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Employee with id 1 does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

    @Test
    void saveEmployeeTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtils.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenReturn(employee);

        EmployeeResponse savedEmployeeResponse = assertDoesNotThrow(() -> employeeService.saveEmployee(employeeRequest));
        AssertionUtils.assertEmployeeResponse(employee, savedEmployeeResponse);

        verify(employeeMapper, times(1)).mapEntityToDto(any(Employee.class));
    }

    @Test
    void updateEmployeeTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtils.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(employeeRepository.saveAndFlush(any(Employee.class))).thenReturn(employee);

        EmployeeResponse employeeResponse = assertDoesNotThrow(() -> employeeService.updateEmployee(1L, employeeRequest));
        AssertionUtils.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void findEmployeesByBranchIdTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findAllEmployeesByBranchId(anyLong())).thenReturn(List.of(employee));

        List<EmployeeResponse> employeeResponses = assertDoesNotThrow(() -> employeeService.findEmployeesByBranchId(1L));
        AssertionUtils.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

    @Test
    void findEmployeeByFilterTest_success() {
        Employee employee = TestUtils.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findByFilter(anyString())).thenReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = assertDoesNotThrow(() -> employeeService.findEmployeeByFilter("Ion"));
        AssertionUtils.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void findEmployeeByFilterTest_errorOnFindingByFilter() {
        when(employeeRepository.findByFilter(anyString())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> employeeService.findEmployeeByFilter("Test"));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Employee with filter: Test does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

}
