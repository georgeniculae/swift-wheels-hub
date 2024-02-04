package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.CarService;
import com.swiftwheelshub.dto.CarForUpdateDetails;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.UpdateCarRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    public ResponseEntity<List<CarResponse>> findAllCars() {
        List<CarResponse> carResponses = carService.findAllCars();

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<CarResponse> findCarById(@PathVariable("id") Long id) {
        CarResponse carResponse = carService.findCarById(id);

        return ResponseEntity.ok(carResponse);
    }

    @GetMapping(path = "/make/{make}")
    public ResponseEntity<List<CarResponse>> findCarsByMake(@PathVariable("make") String make) {
        List<CarResponse> carResponses = carService.findCarsByMake(make);

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countCars() {
        Long numberOfCars = carService.countCars();

        return ResponseEntity.ok(numberOfCars);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<CarResponse> addCar(@RequestBody @Valid CarRequest carRequest) {
        CarResponse savedCarResponse = carService.saveCar(carRequest);

        return ResponseEntity.ok(savedCarResponse);
    }

    @PostMapping(path = "/add")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<CarResponse>> addCars(@RequestBody @Valid List<CarRequest> carRequestList) {
        List<CarResponse> savedCarResponses = carService.saveAllCars(carRequestList);

        return ResponseEntity.ok(savedCarResponses);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<CarResponse>> uploadCars(@RequestParam("file") MultipartFile file) {
        List<CarResponse> savedCarResponses = carService.uploadCars(file);

        return ResponseEntity.ok(savedCarResponses);
    }

    @GetMapping(path = "/{id}/availability")
    public ResponseEntity<CarResponse> getAvailableCar(@PathVariable("id") Long id) {
        CarResponse availableCarResponse = carService.getAvailableCar(id);

        return ResponseEntity.ok(availableCarResponse);
    }

    @PutMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<CarResponse> updateCar(@PathVariable("id") Long id, @RequestBody @Valid CarRequest carRequest) {
        CarResponse updatedCarResponse = carService.updateCar(id, carRequest);

        return ResponseEntity.ok(updatedCarResponse);
    }

    @PutMapping(path = "/{id}/change-status")
    public ResponseEntity<CarResponse> updateCarStatus(@PathVariable("id") Long id, @RequestParam CarState carState) {
        CarResponse updatedCarResponse = carService.updateCarStatus(id, carState);

        return ResponseEntity.ok(updatedCarResponse);
    }

    @PutMapping(path = "/update-statuses")
    public ResponseEntity<List<CarResponse>> updateCarsStatus(@RequestBody @Valid List<UpdateCarRequest> carsForUpdate) {
        List<CarResponse> updatedCarResponses = carService.updateCarsStatus(carsForUpdate);

        return ResponseEntity.ok(updatedCarResponses);
    }

    @PutMapping(path = "/{id}/update-after-return")
    public ResponseEntity<CarResponse> updateCarWhenBookingIsClosed(@PathVariable("id") Long id,
                                                                    @RequestBody @Valid CarForUpdateDetails carForUpdateDetails) {
        CarResponse updatedCarResponse = carService.updateCarWhenBookingIsClosed(id, carForUpdateDetails);

        return ResponseEntity.ok(updatedCarResponse);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Void> deleteCarById(@PathVariable("id") Long id) {
        carService.deleteCarById(id);

        return ResponseEntity.noContent().build();
    }

}
