package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.entity.Revenue;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Stream;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

    @Query("""
            From Revenue revenue""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Revenue> findAllRevenues();

    @Query("""
            From Revenue revenue where
            revenue.dateOfRevenue = ?1""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Revenue> findByDateOfRevenue(LocalDate dateOfRevenue);

    @Query("""
            SELECT sum(revenue.amountFromBooking)
            from Revenue revenue""")
    BigDecimal getTotalAmount();

}
