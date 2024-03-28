package com.swiftwheelshub.expense.util;

import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.Revenue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoiceResponse(Invoice invoice, InvoiceResponse invoiceResponse) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceResponse.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceResponse.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), invoiceResponse.damageCost());
        assertEquals(invoice.getAdditionalPayment(), invoiceResponse.additionalPayment());
        assertEquals(invoice.getTotalAmount(), invoiceResponse.totalAmount());
        assertEquals(invoice.getComments(), invoiceResponse.comments());
    }

    public static void assertRevenueResponse(Revenue revenue, RevenueResponse revenueResponse) {
        assertEquals(revenue.getDateOfRevenue(), revenueResponse.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), revenueResponse.amountFromBooking());
    }

}
