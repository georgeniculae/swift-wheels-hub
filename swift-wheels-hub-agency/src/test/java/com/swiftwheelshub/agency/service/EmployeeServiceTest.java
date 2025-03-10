package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.EmployeeMapper;
import com.swiftwheelshub.agency.mapper.EmployeeMapperImpl;
import com.swiftwheelshub.agency.repository.EmployeeRepository;
import com.swiftwheelshub.agency.util.AssertionUtil;
import com.swiftwheelshub.agency.util.TestUtil;
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
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
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
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findAllEmployee()).thenReturn(Stream.of(employee));

        List<EmployeeResponse> employeeResponses = assertDoesNotThrow(() -> employeeService.findAllEmployees());
        AssertionUtil.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

    @Test
    void findEmployeeByIdTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));

        EmployeeResponse employeeResponse = assertDoesNotThrow(() -> employeeService.findEmployeeById(1L));
        AssertionUtil.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void findEmployeeByIdTest_errorOnFindingById() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> employeeService.findEmployeeById(1L));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Employee with id 1 does not exist", swiftWheelsHubNotFoundException.getReason());
    }

    @Test
    void saveEmployeeTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse savedEmployeeResponse = assertDoesNotThrow(() -> employeeService.saveEmployee(employeeRequest));
        AssertionUtil.assertEmployeeResponse(employee, savedEmployeeResponse);

        verify(employeeMapper).mapEntityToDto(any(Employee.class));
    }

    @Test
    void updateEmployeeTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Branch branch = TestUtil.getResourceAsJson("/data/Branch.json", Branch.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponse employeeResponse = assertDoesNotThrow(() -> employeeService.updateEmployee(1L, employeeRequest));
        AssertionUtil.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void findEmployeesByBranchIdTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findAllEmployeesByBranchId(anyLong())).thenReturn(Stream.of(employee));

        List<EmployeeResponse> employeeResponses = assertDoesNotThrow(() -> employeeService.findEmployeesByBranchId(1L));
        AssertionUtil.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

    @Test
    void findEmployeesByFilterTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        when(employeeRepository.findByFilter(anyString())).thenReturn(Stream.of(employee));

        List<EmployeeResponse> employeeResponses = employeeService.findEmployeesByFilter("Ion");
        AssertionUtil.assertEmployeeResponse(employee, employeeResponses.getFirst());
    }

}
