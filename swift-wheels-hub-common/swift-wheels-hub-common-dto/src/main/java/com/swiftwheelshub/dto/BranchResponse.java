package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;

@Builder
public record BranchResponse(
        Long id,

        @NotEmpty(message = "Name cannot be empty")
        String name,

        String address,

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
