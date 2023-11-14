package com.carrental.emailnotification.util;

import com.carrental.dto.InvoiceDto;
import com.carrental.dto.RevenueDto;
import com.carrental.entity.Invoice;
import com.carrental.entity.Revenue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoice(Invoice invoice, InvoiceDto invoiceDto) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceDto.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceDto.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), invoiceDto.damageCost());
        assertEquals(invoice.getAdditionalPayment(), invoiceDto.additionalPayment());
        assertEquals(invoice.getTotalAmount(), invoiceDto.totalAmount());
        assertEquals(invoice.getComments(), invoiceDto.comments());
    }

    public static void assertRevenue(Revenue revenue, RevenueDto revenueDto) {
        assertEquals(revenue.getDateOfRevenue(), revenueDto.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), revenueDto.amountFromBooking());
    }

}
