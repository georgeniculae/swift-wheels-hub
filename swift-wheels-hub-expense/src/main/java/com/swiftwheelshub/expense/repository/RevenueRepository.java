package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

    @Query("""
            From Revenue revenue where
            revenue.dateOfRevenue = ?1""")
    List<Revenue> findByDateOfRevenue(LocalDate dateOfRevenue);

    @Query("""
            SELECT sum(revenue.amountFromBooking)
            from Revenue revenue""")
    BigDecimal getTotalAmount();

}
