package com.autohub.agency.mapper;

import com.autohub.dto.RentalOfficeRequest;
import com.autohub.dto.RentalOfficeResponse;
import com.autohub.entity.agency.RentalOffice;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RentalOfficeMapper {

    RentalOfficeResponse mapEntityToDto(RentalOffice rentalOffice);

    RentalOffice getNewRentalOffice(RentalOfficeRequest rentalOfficeRequest);

}
