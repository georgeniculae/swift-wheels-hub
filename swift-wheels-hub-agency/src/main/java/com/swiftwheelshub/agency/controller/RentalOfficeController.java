package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.RentalOfficeService;
import com.swiftwheelshub.dto.RentalOfficeRequest;
import com.swiftwheelshub.dto.RentalOfficeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/rental-offices")
public class RentalOfficeController {

    private final RentalOfficeService rentalOfficeService;

    @GetMapping
    public ResponseEntity<List<RentalOfficeResponse>> findAllRentalOffices() {
        List<RentalOfficeResponse> allRentalOffices = rentalOfficeService.findAllRentalOffices();

        return ResponseEntity.ok(allRentalOffices);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RentalOfficeResponse> findRentalOfficeById(@PathVariable("id") Long id) {
        RentalOfficeResponse rentalOfficeResponse = rentalOfficeService.findRentalOfficeById(id);

        return ResponseEntity.ok(rentalOfficeResponse);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countRentalOffices() {
        Long numberOfRentalOffices = rentalOfficeService.countRentalOffices();

        return ResponseEntity.ok(numberOfRentalOffices);
    }

    @PostMapping
    public ResponseEntity<RentalOfficeResponse> addRentalOffice(@RequestBody @Valid RentalOfficeRequest rentalOfficeRequest) {
        RentalOfficeResponse savedRentalOfficeResponse = rentalOfficeService.saveRentalOffice(rentalOfficeRequest);

        return ResponseEntity.ok(savedRentalOfficeResponse);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<RentalOfficeResponse> updateRentalOffice(@PathVariable("id") Long id,
                                                                   @RequestBody @Valid RentalOfficeRequest rentalOfficeRequest) {
        RentalOfficeResponse updatedRentalOfficeResponse = rentalOfficeService.updateRentalOffice(id, rentalOfficeRequest);

        return ResponseEntity.ok(updatedRentalOfficeResponse);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRentalOfficeById(@PathVariable("id") Long id) {
        rentalOfficeService.deleteRentalOfficeById(id);

        return ResponseEntity.noContent().build();
    }

}
