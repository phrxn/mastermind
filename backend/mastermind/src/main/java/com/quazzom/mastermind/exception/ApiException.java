package com.quazzom.mastermind.exception;

/**
 * This class is used to centralize all project exceptions.
 * All custom exceptions in this project MUST inherit from this class.
 */
public abstract class ApiException extends RuntimeException {

    private final int status;

    public ApiException(String message, int status) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}