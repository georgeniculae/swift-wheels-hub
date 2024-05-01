package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Car;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @Autowired
    private CarRepository carRepository;

    @Test
    void checkIfConnectionEstablished() {
        assertTrue(postgres.isCreated());
    }

    @Test
    void findByIdTest_success() {
        Optional<Car> optionalCar = carRepository.findById(1L);
        assertTrue(optionalCar.isPresent());
    }

    @Test
    void findAllCarsTest_success() {
        List<Car> cars = carRepository.findAll();
        assertEquals(2, cars.size());
    }

    @Test
    void findByFilterTest_success() {
        List<Car> cars = carRepository.findByFilter("Golf");
        assertEquals(1, cars.size());
    }

    @Test
    void findCarsByMakeIgnoreCaseTest_success() {
        List<Car> cars = carRepository.findCarsByMakeIgnoreCase("Volkswagen");
        assertEquals(1, cars.size());
    }

    @Test
    void findImageByCarIdTest_success() {
        Optional<Car> optionalCar = carRepository.findImageByCarId(1L);
        assertTrue(optionalCar.isPresent());
    }

}
