package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.RentalOfficeDto;
import com.swiftwheelshub.entity.RentalOffice;
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
