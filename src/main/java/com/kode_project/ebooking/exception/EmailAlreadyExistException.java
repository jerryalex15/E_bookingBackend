package com.kode_project.ebooking.exception;

public class EmailAlreadyExistException extends RuntimeException{
    public EmailAlreadyExistException(String message){
        super(message);
    }
}
