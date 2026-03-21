package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;

class RequestInvalidPropertyValueExceptionTest {

	// ===== constructor / getMessage =====
	@Test
	void constructorShouldStoreCustomMessage() {
		RequestInvalidPropertyValueException exception = new RequestInvalidPropertyValueException("Email format is invalid");

		assertEquals("Email format is invalid", exception.getMessage());
	}

	// ===== status code =====
	@Test
	void shouldHaveStatus422() {
		RequestInvalidPropertyValueException exception = new RequestInvalidPropertyValueException("Value out of range");

		assertEquals(422, exception.getStatus());
	}

	@Test
	void shouldAlwaysReturnStatus422Regardless() {
		RequestInvalidPropertyValueException exception1 = new RequestInvalidPropertyValueException("Invalid age");
		RequestInvalidPropertyValueException exception2 = new RequestInvalidPropertyValueException("Invalid email");
		RequestInvalidPropertyValueException exception3 = new RequestInvalidPropertyValueException("Invalid password");

		assertEquals(422, exception1.getStatus());
		assertEquals(422, exception2.getStatus());
		assertEquals(422, exception3.getStatus());
	}
}
