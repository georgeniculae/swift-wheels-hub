package com.carrental.agency.service;

import com.carrental.agency.mapper.CarMapper;
import com.carrental.agency.repository.CarRepository;
import com.carrental.dto.CarDetailsForUpdateDto;
import com.carrental.dto.CarDto;
import com.carrental.dto.CarForUpdate;
import com.carrental.entity.*;
import com.carrental.exception.CarRentalException;
import com.carrental.exception.CarRentalNotFoundException;
import com.carrental.exception.CarRentalResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
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

    public List<CarDto> findAllCars() {
        return getCarDtoList(carRepository.findAll());
    }

    public CarDto findCarById(Long id) {
        Car car = findEntityById(id);

        return carMapper.mapEntityToDto(car);
    }

    public CarDto getAvailableCar(Long id) {
        Car car = findEntityById(id);
        checkCarAvailability(car);

        return carMapper.mapEntityToDto(car);
    }

    public List<CarDto> findCarsByMake(String make) {
        return getCarDtoList(carRepository.findCarsByMake(make));
    }

    public CarDto saveCar(CarDto carDto) {
        Car car = carMapper.mapDtoToEntity(carDto);

        car.setOriginalBranch(branchService.findEntityById(carDto.originalBranchId()));

        Car savedCar = saveEntity(car);

        return carMapper.mapEntityToDto(savedCar);
    }

    public List<CarDto> saveAllCars(List<CarDto> carDtoList) {
        List<Car> carList = carDtoList.stream()
                .map(carMapper::mapDtoToEntity)
                .toList();

        return getCarDtoList(saveAllEntities(carList));
    }

    public CarDto updateCar(Long id, CarDto updatedCarDto) {
        Car existingCar = findEntityById(id);

        Long branchId = updatedCarDto.originalBranchId();
        Branch branch = branchService.findEntityById(branchId);

        existingCar.setMake(updatedCarDto.make());
        existingCar.setModel(updatedCarDto.model());
        existingCar.setBodyType(Objects.requireNonNull(updatedCarDto.bodyType()));
        existingCar.setYearOfProduction(updatedCarDto.yearOfProduction());
        existingCar.setColor(updatedCarDto.color());
        existingCar.setMileage(updatedCarDto.mileage());
        existingCar.setAmount(Objects.requireNonNull(updatedCarDto.amount()));
        existingCar.setCarStatus(Objects.requireNonNull(updatedCarDto.carStatus()));
        existingCar.setOriginalBranch(branch);
        existingCar.setUrlOfImage(updatedCarDto.urlOfImage());

        Car savedCar = saveEntity(existingCar);

        return carMapper.mapEntityToDto(savedCar);
    }

    public CarDto updateCarStatus(Long id, CarStatus carStatus) {
        Car car = findEntityById(id);
        car.setCarStatus(carStatus);

        Car savedCar = saveEntity(car);

        return carMapper.mapEntityToDto(savedCar);
    }

    public List<CarDto> updateCarsStatus(List<CarForUpdate> carsForUpdate) {
        return carRepository.findAllById(getIds(carsForUpdate))
                .stream()
                .peek(car -> {
                    CarForUpdate carForUpdate = getMatchingCarDetails(carsForUpdate, car);
                    car.setCarStatus(carForUpdate.carStatus());
                })
                .map(this::saveEntity)
                .map(carMapper::mapEntityToDto)
                .toList();
    }

    public List<CarDto> uploadCars(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            List<Car> cars = getCarsFromSheet(sheet);

            return getCarDtoList(carRepository.saveAllAndFlush(cars));
        } catch (Exception e) {
            throw new CarRentalException(e);
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

    public CarDto updateCarWhenBookingIsClosed(Long id, CarDetailsForUpdateDto carDetailsForUpdateDto) {
        Car car = findEntityById(id);
        car.setCarStatus(Objects.requireNonNull(carDetailsForUpdateDto.carStatus()));
        car.setActualBranch(getActualBranch(carDetailsForUpdateDto));

        Car savedCar = saveEntity(car);

        return carMapper.mapEntityToDto(savedCar);
    }

    public void deleteCarById(Long id) {
        carRepository.deleteById(id);
    }

    public CarDto findCarByFilter(String searchString) {
        return carRepository.findByFilter(searchString)
                .map(carMapper::mapEntityToDto)
                .orElseThrow(() -> new CarRentalNotFoundException("Car with filter: " + searchString + " does not exist"));
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
                .orElseThrow(() -> new CarRentalNotFoundException("Car with id " + id + " does not exist"));
    }

    private void checkCarAvailability(Car car) {
        if (!CarStatus.AVAILABLE.equals(car.getCarStatus())) {
            throw new CarRentalResponseStatusException(HttpStatus.BAD_REQUEST, "Selected car is not available");
        }
    }

    private List<Long> getIds(List<CarForUpdate> carsForUpdate) {
        return carsForUpdate.stream()
                .map(CarForUpdate::carId)
                .toList();
    }

    private CarForUpdate getMatchingCarDetails(List<CarForUpdate> carsForUpdate, Car car) {
        return carsForUpdate.stream()
                .filter(carForUpdate -> car.getId().equals(carForUpdate.carId()))
                .findAny()
                .orElseThrow(() -> new CarRentalNotFoundException("Car details not found"));
    }

    private Branch getActualBranch(CarDetailsForUpdateDto carDetailsForUpdateDto) {
        return employeeService.findEntityById(carDetailsForUpdateDto.receptionistEmployeeId()).getWorkingBranch();
    }

    private List<CarDto> getCarDtoList(List<Car> cars) {
        return cars.stream()
                .map(carMapper::mapEntityToDto)
                .toList();
    }

}
