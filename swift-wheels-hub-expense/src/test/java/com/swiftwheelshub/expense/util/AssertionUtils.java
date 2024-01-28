package com.swiftwheelshub.expense.util;

import com.swiftwheelshub.dto.InvoiceRequest;
import com.swiftwheelshub.dto.InvoiceResponse;
import com.swiftwheelshub.dto.RevenueRequest;
import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.Revenue;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertInvoiceRequest(Invoice invoice, InvoiceRequest invoiceRequest) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceRequest.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceRequest.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), Objects.requireNonNull(invoiceRequest.damageCost()).doubleValue());
        assertEquals(invoice.getAdditionalPayment(), Objects.requireNonNull(invoiceRequest.additionalPayment()).doubleValue());
        assertEquals(invoice.getTotalAmount(), Objects.requireNonNull(invoiceRequest.totalAmount()).doubleValue());
        assertEquals(invoice.getComments(), invoiceRequest.comments());
    }

    public static void assertInvoiceResponse(Invoice invoice, InvoiceResponse invoiceResponse) {
        assertEquals(invoice.getCarDateOfReturn(), invoiceResponse.carDateOfReturn());
        assertEquals(invoice.getIsVehicleDamaged(), invoiceResponse.isVehicleDamaged());
        assertEquals(invoice.getDamageCost(), Objects.requireNonNull(invoiceResponse.damageCost()).doubleValue());
        assertEquals(invoice.getAdditionalPayment(), Objects.requireNonNull(invoiceResponse.additionalPayment()).doubleValue());
        assertEquals(invoice.getTotalAmount(), Objects.requireNonNull(invoiceResponse.totalAmount()).doubleValue());
        assertEquals(invoice.getComments(), invoiceResponse.comments());
    }

    public static void assertRevenueRequest(Revenue revenue, RevenueRequest revenueRequest) {
        assertEquals(revenue.getDateOfRevenue(), revenueRequest.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), Objects.requireNonNull(revenueRequest.amountFromBooking()).doubleValue());
    }

    public static void assertRevenueResponse(Revenue revenue, RevenueResponse revenueResponse) {
        assertEquals(revenue.getDateOfRevenue(), revenueResponse.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), Objects.requireNonNull(revenueResponse.amountFromBooking()).doubleValue());
    }

}
