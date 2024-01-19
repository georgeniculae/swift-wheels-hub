package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("""
            From Branch branch
            where lower(branch.name) like '%:filter%' or
            lower(branch.rentalOffice) like '%:filter%'""")
    Optional<Branch> findByFilter(@Param("filter") String filter);

}
