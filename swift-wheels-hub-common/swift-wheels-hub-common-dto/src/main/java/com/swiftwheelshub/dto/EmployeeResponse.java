package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record EmployeeResponse(
        Long id,

        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotEmpty(message = "Job position cannot be empty")
        String jobPosition,

        @NotNull(message = "Working branch id cannot be empty")
        Long workingBranchId
) {

    @Override
    public String toString() {
        return "EmployeeResponse{" + "\n" +
                "id=" + id + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "jobPosition='" + jobPosition + "\n" +
                "workingBranchId=" + workingBranchId + "\n" +
                "}";
    }

}
