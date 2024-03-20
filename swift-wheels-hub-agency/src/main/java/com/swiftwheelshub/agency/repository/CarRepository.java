package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            car.originalBranch,
            car.actualBranch
            )
            From Car car
            where car.id = :id""")
    @NonNull
    Optional<Car> findById(@NonNull Long id);

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            car.originalBranch,
            car.actualBranch
            )
            From Car car
            where lower(car.make) like '%:filter%'
            or lower(car.model) like '%:filter%'""")
    List<Car> findByFilter(@Param("filter") String filter);

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            car.originalBranch,
            car.actualBranch
            )
            From Car car""")
    @NonNull
    List<Car> findAll();

    @Query("""
            Select new Car(
            car.id,
            car.make,
            car.model,
            car.bodyType,
            car.yearOfProduction,
            car.color,
            car.mileage,
            car.carStatus,
            car.amount,
            car.originalBranch,
            car.actualBranch
            )
            From Car car
            where lower(car.make) like '%:make%'""")
    List<Car> findCarsByMakeIgnoreCase(String make);

    @Query("""
            Select new Car(car.image)
            From Car car
            where car.id = :id""")
    Optional<Car> findImageByCarId(@Param("id") Long id);

}
