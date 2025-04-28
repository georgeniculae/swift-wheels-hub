package com.autohub.agency.mapper;

import com.autohub.agency.util.AssertionUtil;
import com.autohub.agency.util.TestUtil;
import com.autohub.dto.EmployeeRequest;
import com.autohub.dto.EmployeeResponse;
import com.autohub.entity.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class EmployeeMapperTest {

    private final EmployeeMapper employeeMapper = new EmployeeMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Employee employee = TestUtil.getResourceAsJson("/data/Employee.json", Employee.class);

        EmployeeResponse employeeResponse = assertDoesNotThrow(() -> employeeMapper.mapEntityToDto(employee));

        assertNotNull(employeeResponse);
        AssertionUtil.assertEmployeeResponse(employee, employeeResponse);
    }

    @Test
    void mapEntityToDtoTest_null() {
        assertNull(employeeMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntityTest_success() {
        EmployeeRequest employeeRequest =
                TestUtil.getResourceAsJson("/data/EmployeeRequest.json", EmployeeRequest.class);

        Employee employee = assertDoesNotThrow(() -> employeeMapper.mapDtoToEntity(employeeRequest));

        assertNotNull(employeeRequest);
        AssertionUtil.assertEmployeeRequest(employee, employeeRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(employeeMapper.mapDtoToEntity(null));
    }

}
