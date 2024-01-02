package com.carrental.exception;

public class CarRentalNotFoundException extends RuntimeException {

    public CarRentalNotFoundException(String message) {
        super(message);
    }

}
