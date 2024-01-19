package com.swiftwheelshub.emailnotification.util;

import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.dto.RevenueDto;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.Revenue;

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
