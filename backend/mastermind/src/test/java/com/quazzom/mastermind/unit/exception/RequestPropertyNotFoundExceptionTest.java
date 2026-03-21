package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;

class RequestPropertyNotFoundExceptionTest {

	// ===== constructor / getMessage =====
	@Test
	void constructorShouldStoreCustomMessage() {
		RequestPropertyNotFoundException exception = new RequestPropertyNotFoundException("Property 'username' is required");

		assertEquals("Property 'username' is required", exception.getMessage());
	}

	// ===== status code =====
	@Test
	void shouldHaveStatus400() {
		RequestPropertyNotFoundException exception = new RequestPropertyNotFoundException("Missing required field");

		assertEquals(400, exception.getStatus());
	}

	@Test
	void shouldAlwaysReturnStatus400Regardless() {
		RequestPropertyNotFoundException exception1 = new RequestPropertyNotFoundException("Field 1 missing");
		RequestPropertyNotFoundException exception2 = new RequestPropertyNotFoundException("Field 2 missing");
		RequestPropertyNotFoundException exception3 = new RequestPropertyNotFoundException("Field 3 missing");

		assertEquals(400, exception1.getStatus());
		assertEquals(400, exception2.getStatus());
		assertEquals(400, exception3.getStatus());
	}
}
