package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.GameFlowException;

class GameFlowExceptionTest {

	// ===== constructor / getMessage =====
	@Test
	void constructorShouldStoreCustomMessage() {
		GameFlowException exception = new GameFlowException("Game is already finished");

		assertEquals("Game is already finished", exception.getMessage());
	}

	// ===== status code =====
	@Test
	void shouldHaveStatus422() {
		GameFlowException exception = new GameFlowException("Invalid game state");

		assertEquals(422, exception.getStatus());
	}

	@Test
	void shouldAlwaysReturnStatus422Regardless() {
		GameFlowException exception1 = new GameFlowException("Message 1");
		GameFlowException exception2 = new GameFlowException("Message 2");
		GameFlowException exception3 = new GameFlowException("Message 3");

		assertEquals(422, exception1.getStatus());
		assertEquals(422, exception2.getStatus());
		assertEquals(422, exception3.getStatus());
	}
}
