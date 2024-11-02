package com.swiftwheelshub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoice", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Invoice extends BaseEntity {

    @NotEmpty(message = "Username cannot be empty")
    private String customerUsername;

    @NotNull(message = "Car id cannot be null")
    private Long carId;

    private Long receptionistEmployeeId;

    private Long returnBranchId;

    @NotNull(message = "Booking id cannot be null")
    private Long bookingId;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Temporal(TemporalType.DATE)
    private LocalDate carReturnDate;

    private Boolean isVehicleDamaged;

    private BigDecimal damageCost;

    private BigDecimal additionalPayment;

    private BigDecimal totalAmount;

    private String comments;

    private InvoiceProcessStatus invoiceProcessStatus;

}
