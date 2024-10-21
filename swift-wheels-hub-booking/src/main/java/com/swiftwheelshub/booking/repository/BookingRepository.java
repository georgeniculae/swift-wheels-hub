package com.swiftwheelshub.booking.repository;

import com.swiftwheelshub.entity.Booking;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            From Booking booking""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Booking> findAllBookings();

    @Query("""
            From Booking booking
            where booking.dateOfBooking = ?1""")
    Optional<Booking> findByDateOfBooking(LocalDate dateOfBooking);

    @Query("""
            From Booking booking
            where booking.customerUsername = ?1""")
    List<Booking> findByCustomerUsername(String customerUsername);

    @Query("""
            Select sum(booking.amount)
            From Booking booking
            where booking.customerUsername = ?1""")
    BigDecimal sumAmountSpentByLoggedInUser(String username);

    @Query("""
            Select sum(booking.amount)
            From Booking booking""")
    BigDecimal sumAllBookingsAmount();

    @Query("""
            From Booking booking
            where booking.customerUsername = ?1""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Booking> findBookingsByUser(String username);

    @Query("""
            From Booking b
            where cast(b.bookingProcessStatus as string) like 'FAILED%'""")
    List<Booking> findAllFailedBookings();

    @Query("""
            Select count(booking)
            From Booking booking
            where booking.customerUsername = ?1""")
    Long countByCustomerUsername(String customerUsername);

    @Query("""
            Select count(distinct b.customerUsername)
            From Booking b""")
    long countUsersWithBookings();

    @Query("""
            Select (count(b) > 0)
            from Booking b
            where b.customerUsername = ?1
            and b.status = 'IN_PROGRESS'""")
    boolean existsInProgressBookingsByCustomerUsername(String customerUsername);

    @Transactional
    @Modifying
    @Query("""
            Delete
            From Booking b
            where b.customerUsername = ?1""")
    void deleteByCustomerUsername(String customerUsername);

}
