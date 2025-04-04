package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.AvailableCarInfo;
import com.swiftwheelshub.dto.BodyCategory;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.entity.BodyType;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.entity.CarStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import org.apache.commons.lang3.ObjectUtils;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface CarMapper {

    @Mapping(target = "originalBranchId", expression = "java(car.getOriginalBranch().getId())")
    @Mapping(target = "actualBranchId", expression = "java(car.getActualBranch().getId())")
    @Mapping(target = "bodyCategory", source = "bodyType")
    @Mapping(target = "carState", source = "carStatus")
    CarResponse mapEntityToDto(Car car);

    @Mapping(target = "bodyType", expression = "java(mapToBodyType(carRequest.bodyCategory()))")
    @Mapping(target = "carStatus", expression = "java(mapToCarStatus(carRequest.carState()))")
    @Mapping(target = "image", expression = "java(mapToImage(image))")
    Car mapDtoToEntity(CarRequest carRequest, MultipartFile image);

    @Mapping(target = "actualBranchId", expression = "java(car.getActualBranch().getId())")
    AvailableCarInfo mapToAvailableCarInfo(Car car);

    default BodyType mapToBodyType(BodyCategory bodyCategory) {
        return BodyType.valueOf(bodyCategory.name());
    }

    default CarStatus mapToCarStatus(CarState carState) {
        return CarStatus.valueOf(carState.name());
    }

    default byte[] mapToImage(MultipartFile multipartFile) {
        try {
            if (ObjectUtils.isEmpty(multipartFile)) {
                return null;
            }

            return multipartFile.getBytes();
        } catch (IOException e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }
    }

}
