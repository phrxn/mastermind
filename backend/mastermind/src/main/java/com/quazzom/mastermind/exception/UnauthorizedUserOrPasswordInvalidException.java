package com.quazzom.mastermind.exception;

public class UnauthorizedUserOrPasswordInvalidException extends UnauthorizedException {

	public UnauthorizedUserOrPasswordInvalidException() {
		super("Usuário ou senha inválidos");
	}
}
