package com.swiftwheelshub.expense.util;

import com.swiftwheelshub.dto.InvoiceDto;
import com.swiftwheelshub.dto.RevenueRequest;
import com.swiftwheelshub.dto.RevenueResponse;
import com.swiftwheelshub.entity.Invoice;
import com.swiftwheelshub.entity.Revenue;

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

    public static void assertRevenueRequest(Revenue revenue, RevenueRequest revenueRequest) {
        assertEquals(revenue.getDateOfRevenue(), revenueRequest.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), Objects.requireNonNull(revenueRequest.amountFromBooking()).doubleValue());
    }

    public static void assertRevenueResponse(Revenue revenue, RevenueResponse revenueResponse) {
        assertEquals(revenue.getDateOfRevenue(), revenueResponse.dateOfRevenue());
        assertEquals(revenue.getAmountFromBooking(), Objects.requireNonNull(revenueResponse.amountFromBooking()).doubleValue());
    }

}
