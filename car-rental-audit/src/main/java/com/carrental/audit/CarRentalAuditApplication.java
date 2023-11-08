package com.carrental.audit;

import com.carrental.lib.annotation.CarRentalMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalMicroservice
public class CarRentalAuditApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalAuditApplication.class);
    }

}
