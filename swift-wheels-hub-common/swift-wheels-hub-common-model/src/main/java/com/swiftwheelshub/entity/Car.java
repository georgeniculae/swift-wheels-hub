package com.swiftwheelshub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.math.BigDecimal;
import java.sql.Types;

@Entity
@Table(name = "car", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Car extends BaseEntity {

    @NotEmpty(message = "Make cannot be empty")
    private String make;

    @NotEmpty(message = "Model cannot be empty")
    private String model;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    @NotNull(message = "Year of production cannot be null")
    private Integer yearOfProduction;

    @NotEmpty(message = "Color cannot be empty")
    private String color;

    @NotNull(message = "Mileage cannot be null")
    private Integer mileage;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Car status cannot be null")
    private CarStatus carStatus;

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_branch_id")
    private Branch originalBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_branch_id")
    private Branch actualBranch;

    @JdbcTypeCode(Types.BINARY)
    private byte[] image;

    public Car(byte[] image) {
        this.image = image;
    }

}
