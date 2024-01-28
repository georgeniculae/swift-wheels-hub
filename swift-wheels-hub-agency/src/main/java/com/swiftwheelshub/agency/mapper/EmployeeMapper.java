package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.EmployeeRequest;
import com.swiftwheelshub.dto.EmployeeResponse;
import com.swiftwheelshub.entity.Employee;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface EmployeeMapper {

    @Mapping(target = "workingBranchId", expression = "java(employee.getWorkingBranch().getId())")
    EmployeeResponse mapEntityToDto(Employee employee);

    Employee mapDtoToEntity(EmployeeRequest employeeRequest);

}
