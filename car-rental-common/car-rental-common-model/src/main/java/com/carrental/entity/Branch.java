package com.carrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "branch", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Branch extends BaseEntity {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    private RentalOffice rentalOffice;

}
