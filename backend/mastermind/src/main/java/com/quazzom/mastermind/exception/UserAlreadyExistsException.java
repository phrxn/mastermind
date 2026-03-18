package com.quazzom.mastermind.exception;

public class UserAlreadyExistsException extends ApiException {

    public UserAlreadyExistsException(String message) {
        super(message, 409);
    }
}