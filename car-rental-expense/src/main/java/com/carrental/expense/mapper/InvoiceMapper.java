package com.carrental.expense.mapper;

import com.carrental.entity.Invoice;
import com.carrental.dto.InvoiceDto;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface InvoiceMapper {

    InvoiceDto mapEntityToDto(Invoice invoice);

    Invoice mapDtoToEntity(InvoiceDto invoiceDto);

}
