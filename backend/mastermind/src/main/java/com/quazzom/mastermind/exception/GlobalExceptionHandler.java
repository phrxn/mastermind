package com.quazzom.mastermind.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException ex) {

        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of(
                        "error", ex.getMessage(),
                        "status", ex.getStatus()
                ));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {

        return ResponseEntity
                .status(405)
                .body(Map.of(
                        "error", "Method not allowed",
                        "status", 405
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {

        return ResponseEntity
                .status(500)
                .body(Map.of(
                        "error", "Internal server error",
                        "status", 500
                ));
    }
}