package com.swiftwheelshub.booking.repository;

import com.swiftwheelshub.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            From Booking booking
            where booking.dateOfBooking = ?1""")
    Optional<Booking> findByDateOfBooking(LocalDate dateOfBooking);

    @Query("""
            From Booking booking
            where booking.customerUsername = ?1""")
    List<Booking> findByCustomerUsername(String customerUsername);

    @Query("""
            From Booking booking
            where booking.customerUsername = ?1""")
    List<Booking> findBookingsByUser(String username);

    @Query("""
            Select count(booking)
            From Booking booking
            where booking.customerUsername = ?1""")
    Long countByCustomerUsername(String customerUsername);

    @Transactional
    @Modifying
    @Query("""
            delete from Booking b
            where b.customerUsername = ?1""")
    void deleteByCustomerUsername(String customerUsername);

}
