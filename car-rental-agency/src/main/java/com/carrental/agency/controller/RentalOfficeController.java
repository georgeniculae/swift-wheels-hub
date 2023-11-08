package com.carrental.agency.controller;

import com.carrental.agency.service.RentalOfficeService;
import com.carrental.dto.RentalOfficeDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
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
@CrossOrigin(origins = "${cross-origin}")
public class RentalOfficeController {

    private final RentalOfficeService rentalOfficeService;

    @GetMapping
    public ResponseEntity<List<RentalOfficeDto>> findAllRentalOffices() {
        List<RentalOfficeDto> rentalOfficeDtoList = rentalOfficeService.findAllRentalOffices();

        return ResponseEntity.ok(rentalOfficeDtoList);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RentalOfficeDto> findRentalOfficeById(@PathVariable("id") Long id) {
        RentalOfficeDto rentalOfficeDto = rentalOfficeService.findRentalOfficeById(id);

        return ResponseEntity.ok(rentalOfficeDto);
    }

    @GetMapping(path = "/count")
    public ResponseEntity<Long> countRentalOffices() {
        Long numberOfRentalOffices = rentalOfficeService.countRentalOffices();

        return ResponseEntity.ok(numberOfRentalOffices);
    }

    @PostMapping
    public ResponseEntity<RentalOfficeDto> addRentalOffice(@RequestBody @Valid RentalOfficeDto rentalOfficeDto) {
        RentalOfficeDto savedRentalOfficeDto = rentalOfficeService.saveRentalOffice(rentalOfficeDto);

        return ResponseEntity.ok(savedRentalOfficeDto);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<RentalOfficeDto> updateRentalOffice(@PathVariable("id") Long id,
                                                              @RequestBody @Valid RentalOfficeDto rentalOfficeDto) {
        RentalOfficeDto updatedRentalOfficeDto = rentalOfficeService.updateRentalOffice(id, rentalOfficeDto);

        return ResponseEntity.ok(updatedRentalOfficeDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deleteRentalOfficeById(@PathVariable("id") Long id) {
        rentalOfficeService.deleteRentalOfficeById(id);

        return ResponseEntity.noContent().build();
    }

}
