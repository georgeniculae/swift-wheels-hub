package com.carrental.agency.util;

import com.carrental.dto.BranchDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.EmployeeDto;
import com.carrental.dto.RentalOfficeDto;
import com.carrental.entity.Branch;
import com.carrental.entity.Car;
import com.carrental.entity.Employee;
import com.carrental.entity.RentalOffice;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AssertionUtils {

    public static void assertCar(Car car, CarDto carDto) {
        assertEquals(car.getMake(), carDto.make());
        assertEquals(car.getModel(), carDto.model());
        assertEquals(car.getBodyType(), Objects.requireNonNull(carDto.bodyType()));
        assertEquals(car.getYearOfProduction(), carDto.yearOfProduction());
        assertEquals(car.getColor(), carDto.color());
        assertEquals(car.getMileage(), carDto.mileage());
        assertEquals(car.getCarStatus(), Objects.requireNonNull(carDto.carStatus()));
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
