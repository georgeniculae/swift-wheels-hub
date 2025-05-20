package com.autohub.expense.mapper;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.dto.expense.InvoiceReprocessRequest;
import com.autohub.expense.entity.Invoice;
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
