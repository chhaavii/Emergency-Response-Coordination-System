package com.emergency.exception;

public class InvalidEmergencyTypeException extends Exception {
    public InvalidEmergencyTypeException(String message) {
        super(message);
    }

    public InvalidEmergencyTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
