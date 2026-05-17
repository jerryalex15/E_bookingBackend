package com.kode_project.ebooking.exception;

public class ServiceNotFoundException extends UserNotFoundException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
}
