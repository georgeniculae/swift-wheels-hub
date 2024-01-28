package com.swiftwheelshub.agency.util;

import com.swiftwheelshub.dto.BranchRequest;
import com.swiftwheelshub.dto.BranchResponse;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.EmployeeDto;
import com.swiftwheelshub.dto.RentalOfficeDto;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.entity.Employee;
import com.swiftwheelshub.entity.RentalOffice;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCarRequest(Car car, CarRequest carRequest) {
        assertEquals(car.getMake(), carRequest.make());
        assertEquals(car.getModel(), carRequest.model());
        assertEquals(car.getBodyType().getDisplayName(), carRequest.bodyCategory().getDisplayName());
        assertEquals(car.getYearOfProduction(), carRequest.yearOfProduction());
        assertEquals(car.getColor(), carRequest.color());
        assertEquals(car.getMileage(), carRequest.mileage());
        assertEquals(car.getCarStatus().getDisplayName(), carRequest.carState().getDisplayName());
        assertEquals(car.getAmount(), Objects.requireNonNull(carRequest.amount()).doubleValue());
        assertEquals(car.getUrlOfImage(), carRequest.urlOfImage());
    }

    public static void assertCarResponse(Car car, CarResponse carResponse) {
        assertEquals(car.getMake(), carResponse.make());
        assertEquals(car.getModel(), carResponse.model());
        assertEquals(car.getBodyType().getDisplayName(), carResponse.bodyCategory().getDisplayName());
        assertEquals(car.getYearOfProduction(), carResponse.yearOfProduction());
        assertEquals(car.getColor(), carResponse.color());
        assertEquals(car.getMileage(), carResponse.mileage());
        assertEquals(car.getCarStatus().getDisplayName(), carResponse.carState().getDisplayName());
        assertEquals(car.getAmount(), Objects.requireNonNull(carResponse.amount()).doubleValue());
        assertEquals(car.getUrlOfImage(), carResponse.urlOfImage());
    }

    public static void assertBranchRequest(Branch branch, BranchRequest branchRequest) {
        assertEquals(branch.getName(), branchRequest.name());
        assertEquals(branch.getAddress(), branchRequest.address());
    }

    public static void assertBranchResponse(Branch branch, BranchResponse branchResponse) {
        assertEquals(branch.getName(), branchResponse.name());
        assertEquals(branch.getAddress(), branchResponse.address());
    }

    public static void assertRentalOffice(RentalOffice rentalOffice, RentalOfficeDto rentalOfficeDto) {
        assertEquals(rentalOffice.getName(), rentalOfficeDto.name());
        assertEquals(rentalOffice.getContactAddress(), rentalOfficeDto.contactAddress());
        assertEquals(rentalOffice.getLogoType(), rentalOfficeDto.logoType());
    }

    public static void assertEmployee(Employee employee, EmployeeDto employeeDto) {
        assertEquals(employee.getFirstName(), employeeDto.firstName());
        assertEquals(employee.getLastName(), employeeDto.lastName());
        assertEquals(employee.getJobPosition(), employeeDto.jobPosition());
        assertEquals(employee.getFirstName(), employeeDto.firstName());
    }

}
