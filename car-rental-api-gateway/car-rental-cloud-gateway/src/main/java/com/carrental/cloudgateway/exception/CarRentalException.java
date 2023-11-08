package com.carrental.cloudgateway.exception;

public class CarRentalException extends RuntimeException {

    public CarRentalException(String message) {
        super(message);
    }

    public CarRentalException(Throwable throwable) {
        super(throwable);
    }

}
