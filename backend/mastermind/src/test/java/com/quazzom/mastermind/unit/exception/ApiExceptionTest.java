package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.ApiException;

class ApiExceptionTest {

	private static class ConcreteApiException extends ApiException {
		public ConcreteApiException(String message, int status) {
			super(message, status);
		}
	}

	// ===== constructor / getMessage =====
	@Test
	void constructorShouldStoreMessage() {
		ApiException exception = new ConcreteApiException("Test message", 400);

		assertEquals("Test message", exception.getMessage());
	}

	// ===== constructor / getStatus =====
	@Test
	void constructorShouldStoreStatus() {
		ApiException exception = new ConcreteApiException("Test message", 400);

		assertEquals(400, exception.getStatus());
	}

	@Test
	void constructorShouldAllowDifferentStatusCodes() {
		ApiException exception401 = new ConcreteApiException("Unauthorized", 401);
		ApiException exception404 = new ConcreteApiException("Not found", 404);
		ApiException exception422 = new ConcreteApiException("Unprocessable", 422);

		assertEquals(401, exception401.getStatus());
		assertEquals(404, exception404.getStatus());
		assertEquals(422, exception422.getStatus());
	}

	// ===== inheritance from RuntimeException =====
	@Test
	void shouldBeInstanceOfRuntimeException() {
		ApiException exception = new ConcreteApiException("Test", 400);

		assertEquals(true, exception instanceof RuntimeException);
	}
}
