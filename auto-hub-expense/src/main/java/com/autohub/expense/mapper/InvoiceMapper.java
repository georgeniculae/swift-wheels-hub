package com.autohub.expense.mapper;

import com.autohub.dto.InvoiceReprocessRequest;
import com.autohub.dto.InvoiceResponse;
import com.autohub.entity.Invoice;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface InvoiceMapper {

    InvoiceResponse mapEntityToDto(Invoice invoice);

    @Mapping(target = "invoiceId", source = "id")
    InvoiceReprocessRequest mapToInvoiceReprocessRequest(Invoice invoice);

}
