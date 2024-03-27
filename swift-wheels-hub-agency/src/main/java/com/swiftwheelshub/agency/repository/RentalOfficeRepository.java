package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.RentalOffice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RentalOfficeRepository extends JpaRepository<RentalOffice, Long> {

    @Query("""
            From RentalOffice rentalOffice
            where upper(rentalOffice.name) like upper(concat('%', ?1, '%'))""")
    List<RentalOffice> findRentalOfficeByName(String rentalOfficeName);

}
