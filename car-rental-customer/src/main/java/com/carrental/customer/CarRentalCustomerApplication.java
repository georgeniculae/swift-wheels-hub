package com.carrental.customer;

import com.carrental.lib.annotation.CarRentalMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalMicroservice
public class CarRentalCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalCustomerApplication.class, args);
    }

}
