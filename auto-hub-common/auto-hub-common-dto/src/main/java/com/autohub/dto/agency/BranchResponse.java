package com.autohub.dto.agency;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record BranchResponse(
        Long id,

        @NotEmpty(message = "Name cannot be empty")
        String name,

        @NotEmpty(message = "Address cannot be empty")
        String address,

        @NotNull(message = "Rental office id cannot be empty")
        Long rentalOfficeId
) {

    @Override
    public String toString() {
        return "BranchResponse{" + "\n" +
                "id=" + id + "\n" +
                ", name='" + name + "\n" +
                ", address='" + address + "\n" +
                ", rentalOfficeId=" + rentalOfficeId + "\n" +
                "}";
    }

}
