package com.hospital.permissiontracking.exception;

public class InsufficientPermissionDaysException extends RuntimeException {

    public InsufficientPermissionDaysException(String message){
        super(message);
    }
}
