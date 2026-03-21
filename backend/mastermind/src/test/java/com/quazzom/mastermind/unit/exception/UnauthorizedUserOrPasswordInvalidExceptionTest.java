package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.UnauthorizedUserOrPasswordInvalidException;

class UnauthorizedUserOrPasswordInvalidExceptionTest {

	// ===== constructor / getMessage =====
	@Test
	void constructorShouldHavePredefinedMessage() {
		UnauthorizedUserOrPasswordInvalidException exception = new UnauthorizedUserOrPasswordInvalidException();

		assertEquals("Usuário ou senha inválidos", exception.getMessage());
	}

	// ===== status code (inherited from UnauthorizedException) =====
	@Test
	void shouldHaveStatus401() {
		UnauthorizedUserOrPasswordInvalidException exception = new UnauthorizedUserOrPasswordInvalidException();

		assertEquals(401, exception.getStatus());
	}

	// ===== inheritance =====
	@Test
	void shouldBeInstanceOfUnauthorizedException() {
		UnauthorizedUserOrPasswordInvalidException exception = new UnauthorizedUserOrPasswordInvalidException();

		assertEquals(true, exception instanceof com.quazzom.mastermind.exception.UnauthorizedException);
	}
}
