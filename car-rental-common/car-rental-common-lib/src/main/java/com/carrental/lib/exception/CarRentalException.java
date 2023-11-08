package com.carrental.lib.exception;

public class CarRentalException extends RuntimeException {

    public CarRentalException(String message) {
        super(message);
    }

    public CarRentalException(Throwable e) {
        super(e);
    }

}
