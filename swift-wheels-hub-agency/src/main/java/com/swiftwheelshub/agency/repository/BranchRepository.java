package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Branch;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.stream.Stream;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    @Query("""
            From Branch branch""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Branch> findAllBranches();

    @Query("""
            From Branch branch
            where upper(branch.name) like upper(concat('%', ?1, '%')) or
            upper(branch.address) like upper(concat('%', ?1, '%')) or
            upper(branch.rentalOffice.name) like upper(concat('%', ?1, '%'))""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Branch> findByFilter(String filter);

}
