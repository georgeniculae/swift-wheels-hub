package com.carrental.booking.mapper;

import com.carrental.dto.CarDto;
import com.carrental.entity.Car;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CarMapper {

    @Mapping(target = "originalBranchId", expression = "java(car.getOriginalBranch().getId())")
    @Mapping(target = "actualBranchId", expression = "java(car.getActualBranch().getId())")
    CarDto mapEntityToDto(Car car);

    Car mapDtoToEntity(CarDto carDto);

}
