package com.carrental.expense;

import com.carrental.lib.annotation.CarRentalMicroservice;
import org.springframework.boot.SpringApplication;

@CarRentalMicroservice
public class CarRentalExpenseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CarRentalExpenseApplication.class, args);
    }

}
