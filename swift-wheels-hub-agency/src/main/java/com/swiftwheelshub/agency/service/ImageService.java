package com.swiftwheelshub.agency.service;

import com.swiftwheelshub.agency.repository.ImageRepository;
import com.swiftwheelshub.entity.Image;
import com.swiftwheelshub.exception.SwiftWheelsHubNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;

    public byte[] getCarImage(Long carId) {
        return imageRepository.findByCarId(carId)
                .map(Image::getContent)
                .orElseThrow(() -> new SwiftWheelsHubNotFoundException("Image not found"));
    }

}
