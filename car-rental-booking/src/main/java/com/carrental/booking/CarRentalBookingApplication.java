package com.carrental.booking;

import com.carrental.lib.annotation.CarRentalMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalMicroservice
public class CarRentalBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalBookingApplication.class, args);
    }

}
