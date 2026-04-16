package com.hospital.permissiontracking.exception;

public class PermissionNotFoundException extends RuntimeException{

    public PermissionNotFoundException(String message){
        super(message);
    }
}
