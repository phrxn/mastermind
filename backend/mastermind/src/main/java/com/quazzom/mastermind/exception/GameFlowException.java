package com.quazzom.mastermind.exception;

public class GameFlowException extends ApiException {

	public GameFlowException(String message) {
		super(message, 422);
	}
}
