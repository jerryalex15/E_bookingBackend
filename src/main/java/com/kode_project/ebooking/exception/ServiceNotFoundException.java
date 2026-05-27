package com.kode_project.ebooking.exception;

public class ServiceNotFoundException extends ResourceNotFoundException {
    public ServiceNotFoundException(String message) {
        super(message);
    }
}
