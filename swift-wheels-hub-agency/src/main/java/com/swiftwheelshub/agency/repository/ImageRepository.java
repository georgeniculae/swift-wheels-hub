package com.swiftwheelshub.agency.repository;

import com.swiftwheelshub.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {

    @Query("""
            SELECT image.content
            FROM Image image
            JOIN Car car ON image.id = car.image.id
            WHERE car.id = : carId""")
    Optional<Image> findByCarId(Long carId);

}
