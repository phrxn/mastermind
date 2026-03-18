package com.quazzom.mastermind.exception;

public class InvalidUserDataException extends ApiException {

    public InvalidUserDataException(String message) {
        super(message, 400);
    }
}
