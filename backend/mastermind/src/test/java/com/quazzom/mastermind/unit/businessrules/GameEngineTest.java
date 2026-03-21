package com.quazzom.mastermind.unit.businessrules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.exception.GameFlowException;

class GameEngineTest {

	private final GameEngine gameEngine = new GameEngine();

	@Test
	void createSecretShouldRespectLengthAndNoDuplicatesWhenDisabled() {
		List<Integer> secret = gameEngine.createSecret(4, false);

		assertEquals(4, secret.size());
		assertEquals(secret.stream().distinct().count(), secret.size());
		assertTrue(secret.stream().allMatch(value -> value >= 1 && value <= 6));
	}

	@Test
	void evaluateShouldReturnCorrectPositionsAndColors() {
		List<Integer> secret = List.of(1, 2, 3, 4);
		List<Integer> guess = List.of(1, 4, 2, 6);

		GameEngineResult result = gameEngine.evaluate(secret, guess);

		assertEquals(1, result.getCorrectPositions());
		assertEquals(2, result.getCorrectColors());
		assertEquals(3, result.getTips().size());
	}

	@Test
	void validateGuessShouldThrowWhenColorIsInvalid() {
		List<Integer> guess = List.of(1, 2, 3, 9);

		assertThrows(GameFlowException.class, () -> gameEngine.validateGuess(guess, 4));
	}

	@Test
	void isWinningShouldReturnTrueOnlyForAllPositions() {
		GameEngineResult winning = new GameEngineResult(4, 0, List.of(1, 1, 1, 1));
		GameEngineResult notWinning = new GameEngineResult(3, 1, List.of(1, 1, 1, 2));

		assertTrue(gameEngine.isWinning(winning, 4));
		assertFalse(gameEngine.isWinning(notWinning, 4));
	}
}
