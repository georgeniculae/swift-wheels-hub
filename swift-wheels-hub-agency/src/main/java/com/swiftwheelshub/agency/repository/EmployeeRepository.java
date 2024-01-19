package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            From Employee employee
            where lower(employee.firstName) like '%:filter%'
            or lower(employee.lastName) like '%:filter%'""")
    Optional<Employee> findByFilter(@Param("filter") String filter);

    @Query("""
            From Employee employee
            where employee.workingBranch.id = :id""")
    List<Employee> findAllEmployeesByBranchId(@Param("id") Long id);

}
