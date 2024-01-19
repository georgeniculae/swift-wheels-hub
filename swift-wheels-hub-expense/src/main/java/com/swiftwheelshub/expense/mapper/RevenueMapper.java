package com.swiftwheelshub.expense.mapper;

import com.swiftwheelshub.dto.RevenueDto;
import com.swiftwheelshub.entity.Revenue;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface RevenueMapper {

    RevenueDto mapEntityToDto(Revenue revenue);

    Revenue mapDtoToEntity(RevenueDto revenueDto);

}
