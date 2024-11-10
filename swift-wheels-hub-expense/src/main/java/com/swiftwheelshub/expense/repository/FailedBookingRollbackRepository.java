package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.expense.model.FailedBookingRollback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FailedBookingRollbackRepository extends JpaRepository<FailedBookingRollback, Long> {

    @Query("""
            select (count(f) == 0)
            from FailedBookingRollback f
            where f.bookingId = ?1""")
    boolean doesNotExistByBookingId(Long bookingId);

}
