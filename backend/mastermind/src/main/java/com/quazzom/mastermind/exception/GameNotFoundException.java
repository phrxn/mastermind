package com.quazzom.mastermind.exception;

public class GameNotFoundException extends ApiException {

	public GameNotFoundException(String message) {
		super(message, 404);
	}
}
