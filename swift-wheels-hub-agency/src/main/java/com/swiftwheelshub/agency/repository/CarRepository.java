package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("""
            From Car car
            where lower(car.make) like '%:filter%'
            or lower(car.model) like '%:filter%'""")
    Optional<Car> findByFilter(@Param("filter") String filter);

    List<Car> findCarsByMake(String make);

}
