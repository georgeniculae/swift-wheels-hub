package com.swiftwheelshub.expense.repository;

import com.swiftwheelshub.entity.Invoice;
import jakarta.persistence.QueryHint;
import org.hibernate.jpa.HibernateHints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;
import java.util.stream.Stream;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @Query("""
            From Invoice invoice
            where invoice.bookingId = ?1""")
    Optional<Invoice> findByBookingId(Long bookingId);

    @Query("""
            From Invoice invoice
            where upper(invoice.comments) like upper(concat('%', ?1, '%'))""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Invoice> findByCommentsIgnoreCase(String comments);

    @Query("""
            From Invoice invoice
            where invoice.customerUsername = ?1 and
            invoice.totalAmount is not null""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Invoice> findByCustomerUsername(String customerUsername);

    @Query("""
            From Invoice invoice
            where invoice.totalAmount is not null""")
    @QueryHints(value = {
            @QueryHint(name = HibernateHints.HINT_FETCH_SIZE, value = "1"),
            @QueryHint(name = HibernateHints.HINT_CACHEABLE, value = "false"),
            @QueryHint(name = HibernateHints.HINT_READ_ONLY, value = "true")
    })
    Stream<Invoice> findAllActive();

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
