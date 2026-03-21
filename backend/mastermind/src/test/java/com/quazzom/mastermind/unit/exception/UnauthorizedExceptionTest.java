package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.UnauthorizedException;

class UnauthorizedExceptionTest {

	// ===== constructor without message / default message =====
	@Test
	void constructorWithoutMessageShouldHaveDefaultMessage() {
		UnauthorizedException exception = new UnauthorizedException();

		assertEquals("Unauthorized", exception.getMessage());
	}

	// ===== constructor with message / custom message =====
	@Test
	void constructorWithMessageShouldStoreCustomMessage() {
		UnauthorizedException exception = new UnauthorizedException("User not authenticated");

		assertEquals("User not authenticated", exception.getMessage());
	}

	// ===== status code =====
	@Test
	void shouldHaveStatus401WhenConstructedWithoutMessage() {
		UnauthorizedException exception = new UnauthorizedException();

		assertEquals(401, exception.getStatus());
	}

	@Test
	void shouldHaveStatus401WhenConstructedWithMessage() {
		UnauthorizedException exception = new UnauthorizedException("Custom message");

		assertEquals(401, exception.getStatus());
	}

	@Test
	void shouldAlwaysReturnStatus401Regardless() {
		UnauthorizedException exception1 = new UnauthorizedException();
		UnauthorizedException exception2 = new UnauthorizedException("Auth failed");
		UnauthorizedException exception3 = new UnauthorizedException("Token expired");

		assertEquals(401, exception1.getStatus());
		assertEquals(401, exception2.getStatus());
		assertEquals(401, exception3.getStatus());
	}
}
