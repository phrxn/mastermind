package com.quazzom.mastermind.exception;

public class UnauthorizedException extends ApiException {

	public UnauthorizedException() {
		super("Unauthorized", 401);
	}

    public UnauthorizedException(String message) {
        super(message, 401);
    }
}