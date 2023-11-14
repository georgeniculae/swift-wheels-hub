package com.carrental.dto;

import jakarta.validation.constraints.NotEmpty;

public record BranchDto(
        Long id,

        @NotEmpty(message = "Name cannot be empty")
        String name,

        String address,

        Long rentalOfficeId
) {

    @Override
    public String toString() {
        return "BranchDto{" + "\n" +
                "id=" + id + "\n" +
                ", name='" + name + "\n" +
                ", address='" + address + "\n" +
                ", rentalOfficeId=" + rentalOfficeId + "\n" +
                "}";
    }

}
