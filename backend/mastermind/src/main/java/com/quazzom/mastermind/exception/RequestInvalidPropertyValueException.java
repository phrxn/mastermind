package com.quazzom.mastermind.exception;

public class RequestInvalidPropertyValueException  extends ApiException {
    public RequestInvalidPropertyValueException(String message) {
        super(message, 422);
    }
}