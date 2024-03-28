package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record InvoiceRequest(
        @NotEmpty(message = "Username cannot be empty")
        String customerUsername,

        @NotEmpty(message = "Customer email cannot be empty")
        String customerEmail,

        @NotNull(message = "Car id cannot be null")
        Long carId,

        Long receptionistEmployeeId,

        @NotNull(message = "Booking id cannot be null")
        Long bookingId,

        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate carDateOfReturn,

        Boolean isVehicleDamaged,

        BigDecimal damageCost,

        BigDecimal additionalPayment,

        BigDecimal totalAmount,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceRequest{" + "\n" +
                "customerUsername='" + customerUsername + "\n" +
                "customerEmail='" + customerEmail + "\n" +
                "carId=" + carId + "\n" +
                "receptionistEmployeeId=" + receptionistEmployeeId + "\n" +
                "bookingId=" + bookingId + "\n" +
                "carDateOfReturn=" + carDateOfReturn + "\n" +
                "isVehicleDamaged=" + isVehicleDamaged + "\n" +
                "damageCost=" + damageCost + "\n" +
                "additionalPayment=" + additionalPayment + "\n" +
                "totalAmount=" + totalAmount + "\n" +
                "comments='" + comments + "\n" +
                "}";
    }

}
