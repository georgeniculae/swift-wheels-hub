package com.autohub.expense.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "revenue", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Revenue extends BaseEntity {

    @NotNull(message = "Date of revenue cannot be null")
    private LocalDate dateOfRevenue;

    @NotNull(message = "Amount from booking cannot be null")
    private BigDecimal amountFromBooking;

}
