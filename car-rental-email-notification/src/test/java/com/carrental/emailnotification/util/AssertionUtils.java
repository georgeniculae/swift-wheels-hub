package com.carrental.emailnotification.util;

import com.carrental.dto.InvoiceDto;
import com.carrental.dto.RevenueDto;
import com.carrental.entity.Invoice;
import com.carrental.entity.Revenue;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoice(Invoice invoice, InvoiceDto invoiceDto) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceDto.getCarDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceDto.getIsVehicleDamaged());
        assertEquals(invoice.getDamageCost(), Objects.requireNonNull(invoiceDto.getDamageCost()).doubleValue());
        assertEquals(invoice.getAdditionalPayment(), Objects.requireNonNull(invoiceDto.getAdditionalPayment()).doubleValue());
        assertEquals(invoice.getTotalAmount(), Objects.requireNonNull(invoiceDto.getTotalAmount()).doubleValue());
        assertEquals(invoice.getComments(), invoiceDto.getComments());
    }

    public static void assertRevenue(Revenue revenue, RevenueDto revenueDto) {
        assertEquals(revenue.getDateOfRevenue(), revenueDto.getDateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), Objects.requireNonNull(revenueDto.getAmountFromBooking()).doubleValue());
    }

}
