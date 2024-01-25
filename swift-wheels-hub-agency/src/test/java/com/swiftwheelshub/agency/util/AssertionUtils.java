package com.swiftwheelshub.agency.util;

import com.swiftwheelshub.dto.BranchDto;
import com.swiftwheelshub.dto.CarDto;
import com.swiftwheelshub.dto.EmployeeDto;
import com.swiftwheelshub.dto.RentalOfficeDto;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.entity.Employee;
import com.swiftwheelshub.entity.RentalOffice;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCar(Car car, CarDto carDto) {
        assertEquals(car.getMake(), carDto.make());
        assertEquals(car.getModel(), carDto.model());
        assertEquals(car.getBodyType().getDisplayName(), carDto.bodyCategory().getDisplayName());
        assertEquals(car.getYearOfProduction(), carDto.yearOfProduction());
        assertEquals(car.getColor(), carDto.color());
        assertEquals(car.getMileage(), carDto.mileage());
        assertEquals(car.getCarStatus().getDisplayName(), carDto.carState().getDisplayName());
        assertEquals(car.getAmount(), Objects.requireNonNull(carDto.amount()).doubleValue());
        assertEquals(car.getUrlOfImage(), carDto.urlOfImage());
    }

    public static void assertBranch(Branch branch, BranchDto branchDto) {
        assertEquals(branch.getName(), branchDto.name());
        assertEquals(branch.getAddress(), branchDto.address());
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
