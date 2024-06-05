package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
            where car.id = ?1""")
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
            where upper(car.make) like upper(concat('%', ?1, '%'))
            or upper(car.model) like upper(concat('%', ?1, '%'))""")
    List<Car> findByFilter(String filter);

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
            where car.carStatus = 'AVAILABLE'""")
    @NonNull
    List<Car> findAllAvailableCars();

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
            where upper(car.make) like upper(concat('%', ?1, '%'))""")
    List<Car> findCarsByMakeIgnoreCase(String make);

    @Query("""
            Select new Car(car.image)
            From Car car
            where car.id = ?1""")
    Optional<Car> findImageByCarId(Long id);

}
