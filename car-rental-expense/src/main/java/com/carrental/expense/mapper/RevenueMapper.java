package com.carrental.expense.mapper;

import com.carrental.entity.Revenue;
import com.carrental.dto.RevenueDto;
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
