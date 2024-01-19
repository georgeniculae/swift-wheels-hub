package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
            From Invoice invoice
            where invoice.bookingId = :bookingId""")
    Optional<Invoice> findByBookingId(@Param("bookingId") Long bookingId);

    @Query("""
            From Invoice invoice
            where invoice.comments like '%:comments%'""")
    List<Invoice> findByComments(@Param("comments") String comments);

    @Query("""
            From Invoice invoice
            where invoice.customerUsername = :customerUsername and
            invoice.totalAmount is not null""")
    List<Invoice> findByCustomerUsername(@Param("customerUsername") String customerUsername);

    @Query("""
            From Invoice invoice
            where invoice.totalAmount is not null""")
    List<Invoice> findAllActive();

    @Query("""
            Select count(invoice)
            From Invoice invoice
            where invoice.totalAmount is not null""")
    Long countAllActive();

    @Query("select (count(i) > 0) from Invoice i where i.bookingId = ?1")
    boolean existsByBookingId(Long bookingId);
}
