package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.EmployeeDto;
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
    EmployeeDto mapEntityToDto(Employee employee);

    Employee mapDtoToEntity(EmployeeDto employeeDto);

}
