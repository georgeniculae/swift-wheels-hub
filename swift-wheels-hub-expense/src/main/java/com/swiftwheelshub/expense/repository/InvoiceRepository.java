package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
            From Invoice invoice
            where invoice.bookingId = ?1""")
    Optional<Invoice> findByBookingId(Long bookingId);

    @Query("""
            From Invoice invoice
            where upper(invoice.comments) like upper(concat('%', ?1, '%'))""")
    List<Invoice> findByCommentsIgnoreCase(String comments);

    @Query("""
            From Invoice invoice
            where invoice.customerUsername = ?1 and
            invoice.totalAmount is not null""")
    List<Invoice> findByCustomerUsername(String customerUsername);

    @Query("""
            From Invoice invoice
            where invoice.totalAmount is not null""")
    List<Invoice> findAllActive();

    @Query("""
            Select count(invoice)
            From Invoice invoice
            where invoice.totalAmount is not null""")
    Long countAllActive();

    @Query("""
            select (count(i) > 0)
            from Invoice i
            where i.bookingId = ?1""")
    boolean existsByBookingId(Long bookingId);

    void deleteByBookingId(Long bookingId);

}
