package com.swiftwheelshub.booking.mapper;

import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.entity.Car;
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
    @Mapping(target = "bodyCategory", source = "bodyType")
    @Mapping(target = "carState", source = "carStatus")
    CarRequest mapEntityToDto(Car car);

    @Mapping(target = "bodyType", source = "bodyCategory")
    @Mapping(target = "carStatus", source = "carState")
    Car mapDtoToEntity(CarRequest carRequest);


}
