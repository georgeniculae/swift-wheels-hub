package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.RentalOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RentalOfficeRepository extends JpaRepository<RentalOffice, Long> {

    @Query("""
            From RentalOffice rentalOffice
            where lower(rentalOffice.name) like '%:rentalOfficeName%'""")
    List<RentalOffice> findRentalOfficeByName(@Param("rentalOfficeName") String rentalOfficeName);

}
