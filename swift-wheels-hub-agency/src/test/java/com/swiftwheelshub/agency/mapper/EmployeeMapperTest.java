package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtil;
import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.entity.Employee;
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
        AssertionUtils.assertEmployeeResponse(employee, employeeResponse);
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
        AssertionUtils.assertEmployeeRequest(employee, employeeRequest);
    }

    @Test
    void mapDtoToEntityTest_null() {
        assertNull(employeeMapper.mapDtoToEntity(null));
    }

}
