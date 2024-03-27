package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("""
            From Branch branch
            where upper(branch.name) like upper(concat('%', ?1, '%')) or
            upper(branch.rentalOffice) like upper(concat('%', ?1, '%'))""")
    List<Branch> findByFilter(String filter);

}
