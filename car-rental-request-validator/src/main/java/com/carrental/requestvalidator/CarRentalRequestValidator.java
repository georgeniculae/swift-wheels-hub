package com.carrental.requestvalidator;

import com.carrental.lib.annotation.CarRentalMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalMicroservice
public class CarRentalRequestValidator {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalRequestValidator.class);
    }

}
