package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.CarService;
import com.swiftwheelshub.dto.CarDetailsForUpdateDto;
import com.swiftwheelshub.dto.CarDto;
import com.swiftwheelshub.dto.CarForUpdate;
import com.swiftwheelshub.entity.CarStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/cars")
public class CarController {

    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<CarDto>> findAllCars() {
        List<CarDto> carDtoList = carService.findAllCars();

        return ResponseEntity.ok(carDtoList);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CarDto> findCarById(@PathVariable("id") Long id) {
        CarDto carDto = carService.findCarById(id);

        return ResponseEntity.ok(carDto);
    }

    @GetMapping(path = "/make/{make}")
    public ResponseEntity<List<CarDto>> findCarsByMake(@PathVariable("make") String make) {
        List<CarDto> carDtoList = carService.findCarsByMake(make);

        return ResponseEntity.ok(carDtoList);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countCars() {
        Long numberOfCars = carService.countCars();

        return ResponseEntity.ok(numberOfCars);
    }

    @PostMapping
    public ResponseEntity<CarDto> addCar(@RequestBody @Valid CarDto carDto) {
        CarDto savedCarDto = carService.saveCar(carDto);

        return ResponseEntity.ok(savedCarDto);
    }

    @PostMapping(path = "/add")
    public ResponseEntity<List<CarDto>> addCars(@RequestBody @Valid List<CarDto> carDtoList) {
        List<CarDto> savedCarDtoList = carService.saveAllCars(carDtoList);

        return ResponseEntity.ok(savedCarDtoList);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<CarDto>> uploadCars(@RequestParam("file") MultipartFile file) {
        List<CarDto> savedCarDtoList = carService.uploadCars(file);

        return ResponseEntity.ok(savedCarDtoList);
    }

    @GetMapping(path = "/{id}/availability")
    public ResponseEntity<CarDto> getAvailableCar(@PathVariable("id") Long id) {
        CarDto availableCarDto = carService.getAvailableCar(id);

        return ResponseEntity.ok(availableCarDto);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable("id") Long id, @RequestBody @Valid CarDto carDto) {
        CarDto updatedCarDto = carService.updateCar(id, carDto);

        return ResponseEntity.ok(updatedCarDto);
    }

    @PutMapping(path = "/{id}/change-car-status")
    public ResponseEntity<CarDto> updateCarStatus(@PathVariable("id") Long id, @RequestParam CarStatus carStatus) {
        CarDto updatedCarDto = carService.updateCarStatus(id, carStatus);

        return ResponseEntity.ok(updatedCarDto);
    }

    @PutMapping(path = "/update-cars-status")
    public ResponseEntity<List<CarDto>> updateCarsStatus(@RequestBody @Valid List<CarForUpdate> carsForUpdate) {
        List<CarDto> updatedCarDtoList = carService.updateCarsStatus(carsForUpdate);

        return ResponseEntity.ok(updatedCarDtoList);
    }

    @PutMapping(path = "/{id}/update-after-closed-booking")
    public ResponseEntity<CarDto> updateCarWhenBookingIsClosed(@PathVariable("id") Long id,
                                                               @RequestBody @Valid CarDetailsForUpdateDto carDetailsForUpdateDto) {
        CarDto updatedCarDto = carService.updateCarWhenBookingIsClosed(id, carDetailsForUpdateDto);

        return ResponseEntity.ok(updatedCarDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteCarById(@PathVariable("id") Long id) {
        carService.deleteCarById(id);

        return ResponseEntity.noContent().build();
    }

}
