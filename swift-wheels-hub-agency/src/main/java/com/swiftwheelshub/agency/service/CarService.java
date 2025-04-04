package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.CarMapper;
import com.swiftwheelshub.agency.repository.CarRepository;
import com.swiftwheelshub.dto.AvailableCarInfo;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarStatusUpdate;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.UpdateCarsRequest;
import com.swiftwheelshub.entity.BodyType;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.entity.CarStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final BranchService branchService;
    private final EmployeeService employeeService;
    private final ExcelParserService excelParserService;
    private final CarMapper carMapper;
    private final ExecutorService executorService;

    @Transactional(readOnly = true)
    public List<CarResponse> findAllCars() {
        try (Stream<Car> cars = carRepository.findAllCars()) {
            return getCarResponses(cars);
        }
    }

    public CarResponse findCarById(Long id) {
        Car car = findEntityById(id);

        return carMapper.mapEntityToDto(car);
    }

    public AvailableCarInfo findAvailableCar(Long id) {
        Car car = findEntityById(id);
        checkCarAvailability(car);

        return carMapper.mapToAvailableCarInfo(car);
    }

    @Transactional(readOnly = true)
    public List<CarResponse> findAllAvailableCars() {
        try (Stream<Car> allAvailableCars = carRepository.findAllAvailableCars()) {
            return allAvailableCars.map(carMapper::mapEntityToDto).toList();
        }
    }

    @Transactional(readOnly = true)
    public List<CarResponse> findCarsByMake(String make) {
        try (Stream<Car> cars = carRepository.findCarsByMakeIgnoreCase(make)) {
            return getCarResponses(cars);
        }
    }

    public byte[] getCarImage(Long id) {
        return carRepository.findImageByCarId(id)
                .map(Car::getImage)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Image not found. Image possible is not attached to car"));
    }

    public CarResponse saveCar(CarRequest carRequest, MultipartFile image) {
        Car car = carMapper.mapDtoToEntity(carRequest, image);
        car.setOriginalBranch(branchService.findEntityById(carRequest.originalBranchId()));
        car.setActualBranch(branchService.findEntityById(carRequest.actualBranchId()));

        Car savedCar = saveEntity(car);

        return carMapper.mapEntityToDto(savedCar);
    }

    public CarResponse updateCar(Long id, CarRequest updatedCarRequest, MultipartFile image) {
        Future<Car> existingCarFuture = getFuture(() -> findEntityById(id));
        Future<Branch> originalBranch = getFuture(() -> branchService.findEntityById(updatedCarRequest.originalBranchId()));
        Future<Branch> actualBranch = getFuture(() -> branchService.findEntityById(updatedCarRequest.actualBranchId()));

        Car existingCar = getFutureResult(existingCarFuture);

        existingCar.setOriginalBranch(getFutureResult(originalBranch));
        existingCar.setActualBranch(getFutureResult(actualBranch));
        existingCar.setMake(updatedCarRequest.make());
        existingCar.setModel(updatedCarRequest.model());
        existingCar.setBodyType(BodyType.valueOf(updatedCarRequest.bodyCategory().name()));
        existingCar.setYearOfProduction(updatedCarRequest.yearOfProduction());
        existingCar.setColor(updatedCarRequest.color());
        existingCar.setMileage(updatedCarRequest.mileage());
        existingCar.setAmount(updatedCarRequest.amount());
        existingCar.setCarStatus(CarStatus.valueOf(updatedCarRequest.carState().name()));
        existingCar.setImage(carMapper.mapToImage(image));

        Car savedCar = saveEntity(existingCar);

        return carMapper.mapEntityToDto(savedCar);
    }

    public void updateCarStatus(CarStatusUpdate carStatusUpdate) {
        Car car = findEntityById(carStatusUpdate.carId());
        car.setCarStatus(CarStatus.valueOf(carStatusUpdate.carState().name()));

        saveEntity(car);
    }

    public List<CarResponse> updateCarsStatus(UpdateCarsRequest updateCarsRequest) {
        List<Car> updatableCars = getUpdatableCars(updateCarsRequest);

        return carRepository.saveAll(updatableCars)
                .stream()
                .map(carMapper::mapEntityToDto)
                .toList();
    }

    public List<CarResponse> uploadCars(MultipartFile file) {
        List<Car> cars = excelParserService.extractDataFromExcel(file);
        List<Car> savedCars = carRepository.saveAll(cars);

        return getCarResponses(savedCars.stream());
    }

    public void updateCarWhenBookingIsClosed(CarUpdateDetails carUpdateDetails) {
        Car car = findEntityById(carUpdateDetails.carId());
        car.setCarStatus(CarStatus.valueOf(carUpdateDetails.carState().name()));
        car.setActualBranch(getActualBranch(carUpdateDetails));

        saveEntity(car);
    }

    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CarResponse> findCarsByFilter(String filter) {
        try (Stream<Car> carStream = carRepository.findByFilter(filter)) {
            return carStream.map(carMapper::mapEntityToDto).toList();
        }
    }

    public Long countCars() {
        return carRepository.count();
    }

    private Car saveEntity(Car car) {
        return carRepository.save(car);
    }

    private Car findEntityById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Car with id " + id + " does not exist"));
    }

    private void checkCarAvailability(Car car) {
        if (!CarStatus.AVAILABLE.equals(car.getCarStatus())) {
            throw new SwiftWheelsHubResponseStatusException(HttpStatus.BAD_REQUEST, "Selected car is not available");
        }
    }

    private List<Car> getUpdatableCars(UpdateCarsRequest updateCarsRequest) {
        return carRepository.findAllById(getIds(updateCarsRequest))
                .stream()
                .peek(car -> car.setCarStatus(getUpdatedCarStatus(updateCarsRequest, car)))
                .toList();
    }

    private List<Long> getIds(UpdateCarsRequest updateCarsRequest) {
        return List.of(
                updateCarsRequest.previousCarId(),
                updateCarsRequest.actualCarId()
        );
    }

    private <T> Future<T> getFuture(Callable<T> branchCallable) {
        return executorService.submit(branchCallable);
    }

    private <T> T getFutureResult(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SwiftWheelsHubException(e.getMessage());
        } catch (ExecutionException e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }
    }

    private CarStatus getUpdatedCarStatus(UpdateCarsRequest updateCarRequests, Car car) {
        if (car.getId().equals(updateCarRequests.previousCarId())) {
            return CarStatus.AVAILABLE;
        }

        return CarStatus.NOT_AVAILABLE;
    }

    private Branch getActualBranch(CarUpdateDetails carUpdateDetails) {
        return employeeService.findEntityById(carUpdateDetails.receptionistEmployeeId()).getWorkingBranch();
    }

    private List<CarResponse> getCarResponses(Stream<Car> cars) {
        return cars.map(carMapper::mapEntityToDto)
                .toList();
    }

}
