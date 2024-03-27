package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.RentalOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RentalOfficeRepository extends JpaRepository<RentalOffice, Long> {

    @Query("""
            From RentalOffice rentalOffice
            where upper(rentalOffice.name) like upper(concat('%', ?1, '%'))
            or upper(rentalOffice.contactAddress) like upper(concat('%', ?1, '%'))
            or upper(rentalOffice.phoneNumber) like upper(concat('%', ?1, '%'))""")
    List<RentalOffice> findRentalOfficeByFilter(String filter);

}
