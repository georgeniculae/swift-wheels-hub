package com.autohub.agency.mapper;

import com.autohub.dto.BranchRequest;
import com.autohub.dto.BranchResponse;
import com.autohub.entity.Branch;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface BranchMapper {

    @Mapping(target = "rentalOfficeId", expression = "java(branch.getRentalOffice().getId())")
    BranchResponse mapEntityToDto(Branch branch);

    Branch mapDtoToEntity(BranchRequest branchRequest);

}
