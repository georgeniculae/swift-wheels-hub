package com.swiftwheelshub.expense.mapper;

import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.entity.Revenue;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface RevenueMapper {

    RevenueResponse mapEntityToDto(Revenue revenue);

}
