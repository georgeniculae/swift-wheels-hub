package com.swiftwheelshub.entity;

import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "rental_office_id")
    private RentalOffice rentalOffice;

}
