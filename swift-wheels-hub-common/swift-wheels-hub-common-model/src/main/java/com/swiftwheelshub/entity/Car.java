package com.swiftwheelshub.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @NotEmpty(message = "Model cannot be null")
    private String model;

    @Enumerated(EnumType.STRING)
    private BodyType bodyType;

    private int yearOfProduction;

    private String color;

    private int mileage;

    @Enumerated(EnumType.STRING)
    private CarStatus carStatus;

    private Double amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "original_branch_id")
    private Branch originalBranch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actual_branch_id")
    private Branch actualBranch;

    private String urlOfImage;

}
