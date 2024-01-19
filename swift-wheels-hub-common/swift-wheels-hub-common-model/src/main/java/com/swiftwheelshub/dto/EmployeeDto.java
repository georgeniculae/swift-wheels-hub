package com.swiftwheelshub.dto;

import jakarta.validation.constraints.NotEmpty;

public record EmployeeDto(
        Long id,

        @NotEmpty(message = "First name cannot be empty")
        String firstName,

        @NotEmpty(message = "Last name cannot be empty")
        String lastName,

        @NotEmpty(message = "Job position cannot be empty")
        String jobPosition,

        Long workingBranchId
) {

    @Override
    public String toString() {
        return "EmployeeDto{" + "\n" +
                "id=" + id + "\n" +
                "firstName='" + firstName + "\n" +
                "lastName='" + lastName + "\n" +
                "jobPosition='" + jobPosition + "\n" +
                "workingBranchId=" + workingBranchId + "\n" +
                "}";
    }

}
