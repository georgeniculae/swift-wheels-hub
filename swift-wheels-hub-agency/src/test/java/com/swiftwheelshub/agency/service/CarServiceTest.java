package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.CarMapper;
import com.swiftwheelshub.agency.mapper.CarMapperImpl;
import com.swiftwheelshub.agency.repository.CarRepository;
import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.CarDto;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        CarDto actualCarDto = Assertions.assertDoesNotThrow(() -> carService.findCarById(1L));

        assertNotNull(actualCarDto);
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

        List<CarDto> carDtoList = Assertions.assertDoesNotThrow(() -> carService.findCarsByMake("Test"));

        assertNotNull(carDtoList);
        verify(carMapper, times(1)).mapEntityToDto(any(Car.class));
    }

    @Test
    void saveCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(carRepository.saveAndFlush(any(Car.class))).thenReturn(car);

        CarDto savedCarDto = Assertions.assertDoesNotThrow(() -> carService.saveCar(carDto));
        AssertionUtils.assertCar(car, savedCarDto);
    }

    @Test
    void saveAllCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);
        List<Car> cars = List.of(car);
        List<CarDto> carDtoList = List.of(carDto);

        when(carRepository.saveAllAndFlush(anyList())).thenReturn(cars);

        List<CarDto> savedCarDtoList = Assertions.assertDoesNotThrow(() -> carService.saveAllCars(carDtoList));
        assertEquals(carDto, savedCarDtoList.getFirst());
    }

    @Test
    void updateCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarDto carDto = TestUtils.getResourceAsJson("/data/CarDto.json", CarDto.class);

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.saveAndFlush(any(Car.class))).thenReturn(car);

        CarDto updatedCarDto = Assertions.assertDoesNotThrow(() -> carService.updateCar(1L, carDto));
        assertNotNull(updatedCarDto);
    }

    @Test
    void findAllCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findAll()).thenReturn(List.of(car));

        List<CarDto> carDtoList = Assertions.assertDoesNotThrow(() -> carService.findAllCars());
        AssertionUtils.assertCar(car, carDtoList.getFirst());
    }

    @Test
    void findCarByFilterTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findByFilter(anyString())).thenReturn(Optional.of(car));

        CarDto carDto = Assertions.assertDoesNotThrow(() -> carService.findCarByFilter("Test"));

        AssertionUtils.assertCar(car, carDto);
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
