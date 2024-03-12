package com.swiftwheelshub.agency.mapper;

import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.entity.Image;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CarMapper {

    @Mapping(target = "originalBranchId", expression = "java(car.getOriginalBranch().getId())")
    @Mapping(target = "actualBranchId", expression = "java(car.getActualBranch().getId())")
    @Mapping(target = "imageId", expression = "java(car.getImage().getId())")
    @Mapping(target = "bodyCategory", source = "bodyType")
    @Mapping(target = "carState", source = "carStatus")
    CarResponse mapEntityToDto(Car car);

    @Mapping(target = "bodyType", source = "bodyCategory")
    @Mapping(target = "carStatus", source = "carState")
    Car mapDtoToEntity(CarRequest carRequest);

    default Image mapToImage(MultipartFile multipartFile) {
        try {
            return Image.builder()
                    .name(multipartFile.getOriginalFilename())
                    .type(multipartFile.getContentType())
                    .content(multipartFile.getBytes())
                    .build();
        } catch (IOException e) {
            throw new SwiftWheelsHubException(e);
        }
    }

}
