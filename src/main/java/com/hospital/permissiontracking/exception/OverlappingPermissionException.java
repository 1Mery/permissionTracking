package com.hospital.permissiontracking.exception;

public class OverlappingPermissionException extends RuntimeException {
    public OverlappingPermissionException(String message) {
        super(message);
    }
}
