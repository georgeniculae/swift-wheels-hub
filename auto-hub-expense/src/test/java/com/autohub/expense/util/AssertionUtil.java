package com.autohub.expense.util;

import com.autohub.dto.InvoiceResponse;
import com.autohub.dto.RevenueResponse;
import com.autohub.entity.invoice.Invoice;
import com.autohub.entity.invoice.Revenue;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtil {

    public static void assertInvoiceResponse(Invoice invoice, InvoiceResponse invoiceResponse) {
        assertEquals(invoice.getCarReturnDate(), invoiceResponse.carReturnDate());
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
