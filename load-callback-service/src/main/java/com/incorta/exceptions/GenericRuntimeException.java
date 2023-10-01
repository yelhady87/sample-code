package com.incorta.exceptions;

public class GenericRuntimeException extends Exception {

    public GenericRuntimeException(String message) {
        super(message);
    }

    public GenericRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
