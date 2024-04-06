package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.CarMapper;
import com.swiftwheelshub.agency.mapper.CarMapperImpl;
import com.swiftwheelshub.agency.repository.CarRepository;
import com.swiftwheelshub.agency.util.AssertionUtils;
import com.swiftwheelshub.agency.util.TestUtils;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
    void findAllCarsTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findAll()).thenReturn(List.of(car));

        List<CarResponse> carResponses = assertDoesNotThrow(() -> carService.findAllCars());
        AssertionUtils.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void findCarsByFilterTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findByFilter(anyString())).thenReturn(List.of(car));

        List<CarResponse> carResponses = assertDoesNotThrow(() -> carService.findCarsByFilter("Test"));

        AssertionUtils.assertCarResponse(car, carResponses.getFirst());
    }

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

        when(carRepository.findCarsByMakeIgnoreCase(anyString())).thenReturn(List.of(car));

        List<CarResponse> carResponses = assertDoesNotThrow(() -> carService.findCarsByMake("Test"));

        assertNotNull(carResponses);
        verify(carMapper, times(1)).mapEntityToDto(any(Car.class));
    }

    @Test
    void saveCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(carRepository.saveAndFlush(any(Car.class))).thenReturn(car);

        CarResponse savedCarResponse = assertDoesNotThrow(() -> carService.saveCar(carRequest, image));
        AssertionUtils.assertCarResponse(car, savedCarResponse);
    }

    @Test
    void updateCarTest_success() {
        Branch branch = TestUtils.getResourceAsJson("/data/Branch.json", Branch.class);
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);
        CarRequest carRequest = TestUtils.getResourceAsJson("/data/CarRequest.json", CarRequest.class);

        MockMultipartFile image =
                new MockMultipartFile("car", "car.jpg", MediaType.TEXT_PLAIN_VALUE, "car".getBytes());

        when(branchService.findEntityById(anyLong())).thenReturn(branch);
        when(carRepository.findById(anyLong())).thenReturn(Optional.of(car));
        when(carRepository.saveAndFlush(any(Car.class))).thenReturn(car);

        CarResponse updatedCarResponse = carService.updateCar(1L, carRequest, image);
        assertNotNull(updatedCarResponse);
    }

    @Test
    void uploadCarsTest_success() throws IOException {
        File excelFile = new File("src/test/resources/file/Cars.xlsx");

        InputStream stream = new FileInputStream(excelFile);

        MockMultipartFile file =
                new MockMultipartFile("file", excelFile.getName(), MediaType.MULTIPART_FORM_DATA_VALUE, stream);

        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.saveAllAndFlush(anyList())).thenReturn(List.of(car));

        List<CarResponse> carResponses = carService.uploadCars(file);
        AssertionUtils.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void uploadCarsTest_errorWhileSavingCars() throws IOException {
        File excelFile = new File("src/test/resources/file/Cars.xlsx");
        InputStream stream = new FileInputStream(excelFile);
        MockMultipartFile file = new MockMultipartFile("file", excelFile.getName(), MediaType.ALL_VALUE, stream);

        when(carRepository.saveAllAndFlush(anyList())).thenThrow(new SwiftWheelsHubException("error"));

        SwiftWheelsHubException swiftWheelsHubException =
                assertThrows(SwiftWheelsHubException.class, () -> carService.uploadCars(file));

        assertNotNull(swiftWheelsHubException);
    }

    @Test
    void updateCarsStatusTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        UpdateCarRequest updateCarRequest =
                TestUtils.getResourceAsJson("/data/UpdateCarRequest.json", UpdateCarRequest.class);

        when(carRepository.findAllById(anyList())).thenReturn(List.of(car));
        when(carRepository.saveAllAndFlush(anyList())).thenReturn(List.of(car));

        List<CarResponse> carResponses = carService.updateCarsStatus(List.of(updateCarRequest));
        AssertionUtils.assertCarResponse(car, carResponses.getFirst());
    }

    @Test
    void updateCarStatusTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findById(anyLong())).thenReturn(Optional.ofNullable(car));
        when(carRepository.saveAndFlush(any(Car.class))).thenReturn(car);

        CarResponse carResponse = carService.updateCarStatus(1L, CarState.AVAILABLE);
        AssertionUtils.assertCarResponse(Objects.requireNonNull(car), carResponse);
    }

    @Test
    void findAvailableCarTest_success() {
        Car car = TestUtils.getResourceAsJson("/data/Car.json", Car.class);

        when(carRepository.findById(anyLong())).thenReturn(Optional.ofNullable(car));

        CarResponse carResponse = carService.findAvailableCar(1L);
        AssertionUtils.assertCarResponse(Objects.requireNonNull(car), carResponse);
    }

}
