package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.CarMapper;
import com.swiftwheelshub.agency.mapper.CarMapperImpl;
import com.swiftwheelshub.agency.repository.CarRepository;
import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @InjectMocks
    private CarService carService;

    @Mock
    private CarRepository carRepository;

    @Mock
    private BranchService branchService;

    @Spy
    private CarMapper carMapper = new CarMapperImpl();

    @Test
    void findCarByIdTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));

        CarResponse actualCarResponse = assertDoesNotThrow(() -> carService.findCarById(1L));

        assertNotNull(actualCarResponse);
        verify(carMapper, times(1)).mapEntityToDto(any(Car.class));
    }

    @Test
    void findCarByIdTest_errorOnFindingById() {
        when(carRepository.findById(anyLong())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> carService.findCarById(1L));

        assertNotNull(swiftWheelsHubNotFoundException);
    }

    @Test
    void findCarsByMakeTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findCarsByMake(anyString())).thenReturn(List.of(car));

        List<CarResponse> carResponses = assertDoesNotThrow(() -> carService.findCarsByMake("Test"));

        assertNotNull(carResponses);
        verify(carMapper, times(1)).mapEntityToDto(any(Car.class));
    }

    @Test
    void saveCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(carRepository.saveAndFlush(any(Car.class))).thenReturn(car);

        CarResponse savedCarResponse = assertDoesNotThrow(() -> carService.saveCar(carRequest));
        AssertionUtils.assertCarResponse(car, savedCarResponse);
    }

    @Test
    void saveAllCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);
        List<Car> cars = List.of(car);
        List<CarRequest> carRequests = List.of(carRequest);

        when(carRepository.saveAllAndFlush(anyList())).thenReturn(cars);

        List<CarResponse> savedCarResponses = assertDoesNotThrow(() -> carService.saveAllCars(carRequests));
        assertFalse(savedCarResponses.isEmpty());
    }

    @Test
    void updateCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.saveAndFlush(any(Car.class))).thenReturn(car);

        CarResponse updatedCarResponse = assertDoesNotThrow(() -> carService.updateCar(1L, carRequest));
        assertNotNull(updatedCarResponse);
    }

    @Test
    void findAllCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findAll()).thenReturn(List.of(car));

        List<CarResponse> carResponses = assertDoesNotThrow(() -> carService.findAllCars());
        AssertionUtils.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void findCarByFilterTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findByFilter(anyString())).thenReturn(Optional.of(car));

        CarResponse carResponse = assertDoesNotThrow(() -> carService.findCarByFilter("Test"));

        AssertionUtils.assertCarResponse(car, carResponse);
    }

    @Test
    void findCarByFilterTest_errorOnFindingByFilter() {
        when(carRepository.findByFilter(anyString())).thenReturn(Optional.empty());

        SwiftWheelsHubNotFoundException swiftWheelsHubNotFoundException =
                assertThrows(SwiftWheelsHubNotFoundException.class, () -> carService.findCarByFilter("Test"));

        assertNotNull(swiftWheelsHubNotFoundException);
        assertEquals("Car with filter: Test does not exist", swiftWheelsHubNotFoundException.getMessage());
    }

}
