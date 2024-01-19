package com.swiftwheelshub.booking.repository;

import com.swiftwheelshub.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
            From Booking booking
            where booking.dateOfBooking = :dateOfBooking""")
    Optional<Booking> findByDateOfBooking(@Param("dateOfBooking") LocalDate dateOfBooking);

    @Query("""
            From Booking booking
            where booking.customerUsername = : username""")
    List<Booking> findBookingsByUser(@Param("username") String username);

    @Query("""
            Select count(booking)
            From Booking booking
            where booking.customerUsername = :customerUsername""")
    Long countByCustomerUsername(@Param("customerUsername") String customerUsername);

    void deleteByCustomerUsername(String customerUsername);

}
