package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.entity.Branch;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BranchMapper {

    @Mapping(target = "rentalOfficeId", expression = "java(branch.getRentalOffice().getId())")
    BranchRequest mapEntityToDto(Branch branch);

    Branch mapDtoToEntity(BranchRequest branchRequest);

}
