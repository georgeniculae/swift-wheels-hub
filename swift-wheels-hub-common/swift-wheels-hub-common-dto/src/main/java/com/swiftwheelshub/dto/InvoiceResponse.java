package com.swiftwheelshub.dto;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
public record InvoiceResponse(
        Long id,

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
        @Temporal(TemporalType.DATE)
        LocalDate carDateOfReturn,

        Boolean isVehicleDamaged,

        Double damageCost,

        Double additionalPayment,

        Double totalAmount,

        String comments
) {

    @Override
    public String toString() {
        return "InvoiceResponse{" + "\n" +
                "id=" + id + "\n" +
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
