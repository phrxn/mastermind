package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.GameNotFoundException;

class GameNotFoundExceptionTest {

	// ===== constructor / getMessage =====
	@Test
	void constructorShouldStoreCustomMessage() {
		GameNotFoundException exception = new GameNotFoundException("Game with id 999 not found");

		assertEquals("Game with id 999 not found", exception.getMessage());
	}

	// ===== status code =====
	@Test
	void shouldHaveStatus404() {
		GameNotFoundException exception = new GameNotFoundException("No game found");

		assertEquals(404, exception.getStatus());
	}

	@Test
	void shouldAlwaysReturnStatus404Regardless() {
		GameNotFoundException exception1 = new GameNotFoundException("Game 1 not found");
		GameNotFoundException exception2 = new GameNotFoundException("Game 2 not found");

		assertEquals(404, exception1.getStatus());
		assertEquals(404, exception2.getStatus());
	}
}
