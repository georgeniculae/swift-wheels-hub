package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.CarService;
import com.swiftwheelshub.dto.CarRequest;
import com.swiftwheelshub.dto.CarResponse;
import com.swiftwheelshub.dto.CarState;
import com.swiftwheelshub.dto.CarUpdateDetails;
import com.swiftwheelshub.dto.UpdateCarRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/cars")
public class CarController {

    private final CarService carService;

    @GetMapping
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> findAllCars() {
        List<CarResponse> carResponses = carService.findAllCars();

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<CarResponse> findCarById(@PathVariable("id") Long id) {
        CarResponse carResponse = carService.findCarById(id);

        return ResponseEntity.ok(carResponse);
    }

    @GetMapping(path = "/make/{make}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> findCarsByMake(@PathVariable("make") String make) {
        List<CarResponse> carResponses = carService.findCarsByMake(make);

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/filter/{filter}")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> findAvailableCar(@PathVariable("filter") String filter) {
        List<CarResponse> carResponses = carService.findCarsByFilter(filter);

        return ResponseEntity.ok(carResponses);
    }

    @GetMapping(path = "/{id}/availability")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<CarResponse> findAvailableCar(@PathVariable("id") Long id) {
        CarResponse availableCarResponse = carService.findAvailableCar(id);

        return ResponseEntity.ok(availableCarResponse);
    }

    @GetMapping(path = "/{id}/image")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<byte[]> findCarImage(@PathVariable("id") Long id) {
        byte[] carImage = carService.getCarImage(id);

        return ResponseEntity.ok(carImage);
    }

    @GetMapping(path = "/count")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<Long> countCars() {
        Long numberOfCars = carService.countCars();

        return ResponseEntity.ok(numberOfCars);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CarResponse> addCar(@RequestPart @Validated CarRequest carRequest,
                                              @RequestPart MultipartFile image) {
        CarResponse savedCarResponse = carService.saveCar(carRequest, image);

        return ResponseEntity.ok(savedCarResponse);
    }

    @PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<CarResponse>> uploadCars(@RequestParam("file") MultipartFile file) {
        List<CarResponse> savedCarResponses = carService.uploadCars(file);

        return ResponseEntity.ok(savedCarResponses);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CarResponse> updateCar(@PathVariable("id") Long id,
                                                 @RequestPart @Validated CarRequest carRequest,
                                                 @RequestPart MultipartFile image) {
        CarResponse updatedCarResponse = carService.updateCar(id, carRequest, image);

        return ResponseEntity.ok(updatedCarResponse);
    }

    @PutMapping(path = "/{id}/change-status")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<CarResponse> updateCarStatus(@PathVariable("id") Long id, @RequestParam CarState carState) {
        CarResponse updatedCarResponse = carService.updateCarStatus(id, carState);

        return ResponseEntity.ok(updatedCarResponse);
    }

    @PutMapping(path = "/update-statuses")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<List<CarResponse>> updateCarsStatus(@RequestBody @Validated List<UpdateCarRequest> carsForUpdate) {
        List<CarResponse> updatedCarResponses = carService.updateCarsStatus(carsForUpdate);

        return ResponseEntity.ok(updatedCarResponses);
    }

    @PutMapping(path = "/{id}/update-after-return")
    @PreAuthorize("hasRole('user')")
    public ResponseEntity<CarResponse> updateCarWhenBookingIsClosed(@PathVariable("id") Long id,
                                                                    @RequestBody @Validated CarUpdateDetails carUpdateDetails) {
        CarResponse updatedCarResponse = carService.updateCarWhenBookingIsClosed(id, carUpdateDetails);

        return ResponseEntity.ok(updatedCarResponse);
    }

    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteCarById(@PathVariable("id") Long id) {
        carService.deleteCarById(id);

        return ResponseEntity.noContent().build();
    }

}
