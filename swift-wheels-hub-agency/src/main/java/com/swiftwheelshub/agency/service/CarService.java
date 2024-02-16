package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.mapper.CarMapper;
import com.swiftwheelshub.agency.repository.CarRepository;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.UpdateCarRequest;
import com.swiftwheelshub.entity.BodyType;
import com.swiftwheelshub.entity.Branch;
import com.swiftwheelshub.entity.Car;
import com.swiftwheelshub.entity.CarFields;
import com.swiftwheelshub.entity.CarStatus;
import com.swiftwheelshub.exception.SwiftWheelsHubException;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import com.swiftwheelshub.exception.SwiftWheelsHubResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;
    private final BranchService branchService;
    private final EmployeeService employeeService;
    private final CarMapper carMapper;

    public List<CarResponse> findAllCars() {
        return getCarResponseList(carRepository.findAll());
    }

    public CarResponse findCarById(Long id) {
        Car car = findEntityById(id);

        return carMapper.mapEntityToDto(car);
    }

    public CarResponse findAvailableCar(Long id) {
        Car car = findEntityById(id);
        checkCarAvailability(car);

        return carMapper.mapEntityToDto(car);
    }

    public List<CarResponse> findCarsByMake(String make) {
        return getCarResponseList(carRepository.findCarsByMake(make));
    }

    public CarResponse saveCar(CarRequest carRequest) {
        Car car = carMapper.mapDtoToEntity(carRequest);

        car.setOriginalBranch(branchService.findEntityById(carRequest.originalBranchId()));

        Car savedCar = saveEntity(car);

        return carMapper.mapEntityToDto(savedCar);
    }

    public List<CarResponse> saveAllCars(List<CarRequest> carRequests) {
        List<Car> cars = carRequests.stream()
                .map(carMapper::mapDtoToEntity)
                .toList();

        return getCarResponseList(saveAllEntities(cars));
    }

    public CarResponse updateCar(Long id, CarRequest updatedCarRequest) {
        Car existingCar = findEntityById(id);

        Long branchId = updatedCarRequest.originalBranchId();
        Branch branch = branchService.findEntityById(branchId);

        existingCar.setMake(updatedCarRequest.make());
        existingCar.setModel(updatedCarRequest.model());
        existingCar.setBodyType(BodyType.valueOf(updatedCarRequest.bodyCategory().name()));
        existingCar.setYearOfProduction(updatedCarRequest.yearOfProduction());
        existingCar.setColor(updatedCarRequest.color());
        existingCar.setMileage(updatedCarRequest.mileage());
        existingCar.setAmount(Objects.requireNonNull(updatedCarRequest.amount()));
        existingCar.setCarStatus(CarStatus.valueOf(updatedCarRequest.carState().name()));
        existingCar.setOriginalBranch(branch);
        existingCar.setUrlOfImage(updatedCarRequest.urlOfImage());

        Car savedCar = saveEntity(existingCar);

        return carMapper.mapEntityToDto(savedCar);
    }

    public CarResponse updateCarStatus(Long id, CarState carState) {
        Car car = findEntityById(id);
        car.setCarStatus(CarStatus.valueOf(carState.name()));

        Car savedCar = saveEntity(car);

        return carMapper.mapEntityToDto(savedCar);
    }

    public List<CarResponse> updateCarsStatus(List<UpdateCarRequest> carsForUpdate) {
        return carRepository.findAllById(getIds(carsForUpdate))
                .stream()
                .peek(car -> {
                    UpdateCarRequest updateCarRequest = getMatchingCarDetails(carsForUpdate, car);
                    car.setCarStatus(CarStatus.valueOf(updateCarRequest.carState().name()));
                })
                .map(this::saveEntity)
                .map(carMapper::mapEntityToDto)
                .toList();
    }

    public List<CarResponse> uploadCars(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            List<Car> cars = getCarsFromSheet(sheet);

            return getCarResponseList(carRepository.saveAllAndFlush(cars));
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e);
        }
    }

    private List<Car> getCarsFromSheet(Sheet sheet) {
        DataFormatter dataFormatter = new DataFormatter();
        List<Car> cars = new ArrayList<>();

        for (int index = 1; index <= sheet.getLastRowNum(); index++) {
            List<Object> values = new ArrayList<>();

            Row currentRow = sheet.getRow(index);
            Iterator<Cell> cellIterator = currentRow.cellIterator();

            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();

                switch (cell.getCellType()) {
                    case STRING -> values.add(cell.getStringCellValue());
                    case NUMERIC -> values.add(dataFormatter.formatCellValue(cell));
                }
            }

            cars.add(generateCar(values));
        }

        return cars;
    }

    private Car generateCar(List<Object> values) {
        return Car.builder()
                .make((String) values.get(CarFields.MAKE.ordinal()))
                .model((String) values.get(CarFields.MODEL.ordinal()))
                .bodyType(BodyType.valueOf(((String) values.get(CarFields.BODY_TYPE.ordinal())).toUpperCase()))
                .yearOfProduction(Integer.parseInt((String) values.get(CarFields.YEAR_OF_PRODUCTION.ordinal())))
                .color((String) values.get(CarFields.COLOR.ordinal()))
                .mileage(Integer.parseInt((String) values.get(CarFields.MILEAGE.ordinal())))
                .carStatus(CarStatus.valueOf(((String) values.get(CarFields.CAR_STATUS.ordinal())).toUpperCase()))
                .amount(Double.valueOf((String) values.get(CarFields.AMOUNT.ordinal())))
                .originalBranch(branchService.findEntityById(Long.valueOf((String) values.get(CarFields.ORIGINAL_BRANCH.ordinal()))))
                .actualBranch(branchService.findEntityById(Long.valueOf((String) values.get(CarFields.ACTUAL_BRANCH.ordinal()))))
                .urlOfImage((String) values.get(CarFields.URL_OF_IMAGE.ordinal()))
                .build();
    }

    public CarResponse updateCarWhenBookingIsClosed(Long id, CarUpdateDetails carUpdateDetails) {
        Car car = findEntityById(id);
        car.setCarStatus(CarStatus.valueOf(carUpdateDetails.carState().name()));
        car.setActualBranch(getActualBranch(carUpdateDetails));

        Car savedCar = saveEntity(car);

        return carMapper.mapEntityToDto(savedCar);
    }

    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    public CarResponse findCarByFilter(String searchString) {
        return carRepository.findByFilter(searchString)
                .map(carMapper::mapEntityToDto)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Car with filter: " + searchString + " does not exist"));
    }

    public Long countCars() {
        return carRepository.count();
    }

    private Car saveEntity(Car car) {
        return carRepository.saveAndFlush(car);
    }

    private List<Car> saveAllEntities(List<Car> carList) {
        return carRepository.saveAllAndFlush(carList);
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

    private List<Long> getIds(List<UpdateCarRequest> carsForUpdate) {
        return carsForUpdate.stream()
                .map(UpdateCarRequest::carId)
                .toList();
    }

    private UpdateCarRequest getMatchingCarDetails(List<UpdateCarRequest> carsForUpdate, Car car) {
        return carsForUpdate.stream()
                .filter(updateCarRequest -> car.getId().equals(updateCarRequest.carId()))
                .findAny()
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Car details not found"));
    }

    private Branch getActualBranch(CarUpdateDetails carUpdateDetails) {
        return employeeService.findEntityById(carUpdateDetails.receptionistEmployeeId()).getWorkingBranch();
    }

    private List<CarResponse> getCarResponseList(List<Car> cars) {
        return cars.stream()
                .map(carMapper::mapEntityToDto)
                .toList();
    }

}
