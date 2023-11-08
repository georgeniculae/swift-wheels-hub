package com.carrental.agency.mapper;

import com.carrental.entity.RentalOffice;
import com.carrental.dto.RentalOfficeDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RentalOfficeMapper {

    RentalOfficeDto mapEntityToDto(RentalOffice rentalOffice);

    RentalOffice mapDtoToEntity(RentalOfficeDto rentalOfficeDto);

}
