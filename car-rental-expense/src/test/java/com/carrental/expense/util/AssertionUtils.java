package com.carrental.expense.util;

import com.carrental.dto.InvoiceDto;
import com.carrental.dto.RevenueDto;
import com.carrental.entity.Invoice;
import com.carrental.entity.Revenue;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoice(Invoice invoice, InvoiceDto invoiceDto) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceDto.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceDto.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), Objects.requireNonNull(invoiceDto.damageCost()).doubleValue());
        assertEquals(invoice.getAdditionalPayment(), Objects.requireNonNull(invoiceDto.additionalPayment()).doubleValue());
        assertEquals(invoice.getTotalAmount(), Objects.requireNonNull(invoiceDto.totalAmount()).doubleValue());
        assertEquals(invoice.getComments(), invoiceDto.comments());
    }

    public static void assertRevenue(Revenue revenue, RevenueDto revenueDto) {
        assertEquals(revenue.getDateOfRevenue(), revenueDto.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), Objects.requireNonNull(revenueDto.amountFromBooking()).doubleValue());
    }

}
