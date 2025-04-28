package com.autohub.agency.mapper;

import com.autohub.dto.EmployeeRequest;
import com.autohub.dto.EmployeeResponse;
import com.autohub.entity.Employee;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface EmployeeMapper {

    @Mapping(target = "workingBranchId", expression = "java(employee.getWorkingBranch().getId())")
    EmployeeResponse mapEntityToDto(Employee employee);

    Employee mapDtoToEntity(EmployeeRequest employeeRequest);

}
