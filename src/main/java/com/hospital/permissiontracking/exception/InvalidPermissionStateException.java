package com.hospital.permissiontracking.exception;

public class InvalidPermissionStateException extends RuntimeException{
    public InvalidPermissionStateException(String message){
        super(message);
    }
}
