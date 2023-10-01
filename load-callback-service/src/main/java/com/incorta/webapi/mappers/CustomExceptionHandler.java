package com.incorta.webapi.mappers;

import com.incorta.exceptions.GenericRuntimeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;
import java.net.URISyntaxException;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(GenericRuntimeException.class)
    public ResponseEntity<ExceptionResponse> handleException(GenericRuntimeException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ExceptionResponse> handleException(Exception ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage());
        return ResponseEntity.status(500).body(response);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ExceptionResponse> handleException(HttpStatusCodeException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode()).body(response);
    }

    @ExceptionHandler(URISyntaxException.class)
    public ResponseEntity<ExceptionResponse> handleException(URISyntaxException ex) {
        ExceptionResponse response = new ExceptionResponse(ex.getMessage());
        return ResponseEntity.status(400).body(response);
    }
}


