package com.emergency.exception;

public class NoAvailableResponderException extends Exception {
    public NoAvailableResponderException(String message) {
        super(message);
    }

    public NoAvailableResponderException(String message, Throwable cause) {
        super(message, cause);
    }
}
