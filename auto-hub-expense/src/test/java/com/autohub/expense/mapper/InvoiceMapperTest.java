package com.autohub.expense.mapper;

import com.autohub.dto.common.InvoiceResponse;
import com.autohub.expense.entity.Invoice;
import com.autohub.expense.util.AssertionUtil;
import com.autohub.expense.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class InvoiceMapperTest {

    private final InvoiceMapper invoiceMapper = new InvoiceMapperImpl();

    @Test
    void mapEntityToDtoTest_success() {
        Invoice invoice = TestUtil.getResourceAsJson("/data/Invoice.json", Invoice.class);

        InvoiceResponse invoiceResponse = invoiceMapper.mapEntityToDto(invoice);

        assertNotNull(invoiceResponse);
        AssertionUtil.assertInvoiceResponse(invoice, invoiceResponse);
    }

}
