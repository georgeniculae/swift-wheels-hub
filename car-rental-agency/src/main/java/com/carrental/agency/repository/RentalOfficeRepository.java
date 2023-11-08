package com.carrental.agency.repository;

import com.carrental.entity.RentalOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RentalOfficeRepository extends JpaRepository<RentalOffice, Long> {

    @Query("""
            From RentalOffice rentalOffice
            where lower(rentalOffice.name) like '%:rentalOfficeName%'""")
    Optional<RentalOffice> findRentalOfficeByName(@Param("rentalOfficeName") String rentalOfficeName);

}
