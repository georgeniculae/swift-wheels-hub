package com.carrental.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "rental_office", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RentalOffice extends BaseEntity {

    @NotEmpty(message = "Name cannot be empty")
    private String name;

    @NotEmpty(message = "Contact address domain cannot be empty")
    private String contactAddress;

    private String logoType;

}
