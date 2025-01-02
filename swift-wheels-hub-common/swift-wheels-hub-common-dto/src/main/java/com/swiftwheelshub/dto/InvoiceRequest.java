package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceRequest(
        @NotNull(message = "Car id cannot be null")
        Long carId,

        Long receptionistEmployeeId,

        @NotNull(message = "Return branch id cannot be null")
        Long returnBranchId,

        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate carReturnDate,

        @NotNull
        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceRequest{" + "\n" +
                "previousCarId=" + carId + "\n" +
                "returnBranchId=" + receptionistEmployeeId + "\n" +
                "bookingId=" + bookingId + "\n" +
                "carReturnDate=" + carReturnDate + "\n" +
                "isVehicleDamaged=" + isVehicleDamaged + "\n" +
                "damageCost=" + damageCost + "\n" +
                "additionalPayment=" + additionalPayment + "\n" +
                "totalAmount=" + totalAmount + "\n" +
                "comments='" + comments + "\n" +
                "}";
    }

}
