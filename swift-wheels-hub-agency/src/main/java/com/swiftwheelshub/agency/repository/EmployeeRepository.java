package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("""
            From Employee employee
            where upper(employee.firstName) like upper(concat('%', ?1, '%'))
            or upper(employee.lastName) like upper(concat('%', ?1, '%'))""")
    List<Employee> findByFilter(String filter);

    @Query("""
            From Employee employee
            where employee.workingBranch.id = ?1""")
    List<Employee> findAllEmployeesByBranchId(Long id);

}
