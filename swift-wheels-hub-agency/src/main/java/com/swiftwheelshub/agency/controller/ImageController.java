package com.swiftwheelshub.agency.controller;

import com.swiftwheelshub.agency.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/images")
public class ImageController {

    private final ImageService imageService;

    @GetMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('user')")
    public ResponseEntity<byte[]> findCarImage(@PathVariable("id") Long id) {
        byte[] carImage = imageService.getCarImage(id);

        return ResponseEntity.ok(carImage);
    }

}
