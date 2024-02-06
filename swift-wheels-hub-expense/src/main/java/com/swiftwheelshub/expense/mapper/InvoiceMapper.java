package com.swiftwheelshub.expense.mapper;

import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.entity.Invoice;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {

    InvoiceResponse mapEntityToDto(Invoice invoice);

}
