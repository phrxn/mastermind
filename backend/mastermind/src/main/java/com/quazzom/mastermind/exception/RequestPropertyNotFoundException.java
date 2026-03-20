package com.quazzom.mastermind.exception;

public class RequestPropertyNotFoundException  extends ApiException {
    public RequestPropertyNotFoundException(String message) {
        super(message, 400);
    }
}