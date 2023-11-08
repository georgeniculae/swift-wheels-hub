package com.carrental.agency;

import com.carrental.lib.annotation.CarRentalMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalMicroservice
public class CarRentalAgencyApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalAgencyApplication.class, args);
    }

}
