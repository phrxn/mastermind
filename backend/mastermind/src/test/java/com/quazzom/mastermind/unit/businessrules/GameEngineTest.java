package com.quazzom.mastermind.unit.businessrules;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.exception.GameFlowException;

class GameEngineTest {

	private final GameEngine gameEngine = new GameEngine();

	// ===== createSecret =====
	@Test
	void createSecretShouldThrowWhenCodeLengthIsZero() {
		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.createSecret(0, false));

		assertEquals("Tamanho do código inválido", exception.getMessage());
	}

	@Test
	void createSecretShouldThrowWhenCodeLengthIsGreaterThanSix() {
		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.createSecret(7, false));

		assertEquals("Tamanho do código inválido", exception.getMessage());
	}

	@Test
	void createSecretShouldRespectLengthAndRangeWhenDuplicatesAreAllowed() {
		List<Integer> secret = gameEngine.createSecret(4, true);

		assertEquals(4, secret.size());
		assertTrue(secret.stream().allMatch(value -> value >= 1 && value <= 6));
	}

	@Test
	void createSecretShouldRespectLengthRangeAndUniquenessWhenDuplicatesAreDisabled() {
		List<Integer> secret = gameEngine.createSecret(6, false);

		assertEquals(6, secret.size());
		assertEquals(secret.size(), secret.stream().distinct().count());
		assertTrue(secret.stream().allMatch(value -> value >= 1 && value <= 6));
	}

	// ===== validateGuess =====
	@Test
	void validateGuessShouldThrowWhenGuessIsNull() {
		Game game = createGame(4, GameLevel.EASY);

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.validateGuess(game, null));

		assertTrue(exception.getMessage().contains("Quantidade esperada de cores: 4"));
	}

	@Test
	void validateGuessShouldThrowWhenGuessSizeIsDifferentFromCodeLength() {
		Game game = createGame(4, GameLevel.EASY);

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.validateGuess(game, List.of(1, 2, 3)));

		assertTrue(exception.getMessage().contains("Quantidade esperada de cores: 4"));
	}

	@Test
	void validateGuessShouldThrowWhenGuessContainsNullColor() {
		Game game = createGame(4, GameLevel.EASY);

		GameFlowException exception = assertThrows(GameFlowException.class,
			() -> gameEngine.validateGuess(game, Arrays.asList(1, null, 3, 4)));

		assertEquals("A propriedade 'guess' contém valores de cor inválidos. As cores devem ser números entre 1 e 6",
				exception.getMessage());
	}

	@Test
	void validateGuessShouldThrowWhenGuessContainsColorLowerThanRange() {
		Game game = createGame(4, GameLevel.EASY);

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.validateGuess(game, List.of(0, 2, 3, 4)));

		assertEquals("A propriedade 'guess' contém valores de cor inválidos. As cores devem ser números entre 1 e 6",
				exception.getMessage());
	}

	@Test
	void validateGuessShouldThrowWhenGuessContainsColorGreaterThanRange() {
		Game game = createGame(4, GameLevel.EASY);

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.validateGuess(game, List.of(1, 2, 3, 7)));

		assertEquals("A propriedade 'guess' contém valores de cor inválidos. As cores devem ser números entre 1 e 6",
				exception.getMessage());
	}

	@Test
	void validateGuessShouldThrowWhenGuessHasRepeatedColorsAndLevelDoesNotAllowDuplicates() {
		Game game = createGame(4, GameLevel.EASY);

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.validateGuess(game, List.of(1, 1, 3, 4)));

		assertEquals("A propriedade 'guess' contém valores de cores repetidas, mas o jogo atual não permite cores repetidas",
				exception.getMessage());
	}

	@Test
	void validateGuessShouldAllowNotRepeatedColorsWhenLevelIsEasy() {
		Game game = createGame(4, GameLevel.EASY);

		assertDoesNotThrow(() -> gameEngine.validateGuess(game, List.of(1, 2, 3, 4)));
	}

	@Test
	void validateGuessShouldThrowWhenLevelIsEasyAndGuessHasRepeatedColors() {
		Game game = createGame(4, GameLevel.EASY);

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.validateGuess(game, List.of(1, 1, 3, 4)));

		assertEquals("A propriedade 'guess' contém valores de cores repetidas, mas o jogo atual não permite cores repetidas",
				exception.getMessage());
	}

	@Test
	void validateGuessShouldAllowRepeatedColorsWhenLevelIsNormal() {
		Game game = createGame(4, GameLevel.NORMAL);

		assertDoesNotThrow(() -> gameEngine.validateGuess(game, List.of(1, 1, 3, 4)));
	}

	@Test
	void validateGuessShouldAllowNotRepeatedColorsWhenLevelIsNormal() {
		Game game = createGame(4, GameLevel.NORMAL);

		assertDoesNotThrow(() -> gameEngine.validateGuess(game, List.of(1, 2, 3, 4)));
	}


	@Test
	void validateGuessShouldAllowNotRepeatedColorsWhenLevelIsHard() {
		Game game = createGame(6, GameLevel.HARD);

		assertDoesNotThrow(() -> gameEngine.validateGuess(game, List.of(1, 2, 3, 4, 5, 6)));
	}

	@Test
	void validateGuessShouldThrowWhenLevelIsHardAndGuessHasRepeatedColors() {
		Game game = createGame(6, GameLevel.HARD);

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.validateGuess(game, List.of(1, 1, 2, 3, 4, 5)));

		assertEquals("A propriedade 'guess' contém valores de cores repetidas, mas o jogo atual não permite cores repetidas",
				exception.getMessage());
	}

	@Test
	void validateGuessShouldAllowRepeatedColorsWhenLevelIsMastermind() {
		Game game = createGame(6, GameLevel.MASTERMIND);

		assertDoesNotThrow(() -> gameEngine.validateGuess(game, List.of(1, 1, 2, 3, 4, 4)));
	}

	@Test
	void validateGuessShouldAllowNotRepeatedColorsWhenLevelIsMastermind() {
		Game game = createGame(6, GameLevel.MASTERMIND);

		assertDoesNotThrow(() -> gameEngine.validateGuess(game, List.of(1, 2, 3, 4, 5, 6)));
	}




	// ===== evaluate =====
	@Test
	void evaluateShouldThrowWhenSecretIsNull() {
		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.evaluate(null, List.of(1, 2, 3, 4)));

		assertEquals("Não foi possível avaliar a tentativa", exception.getMessage());
	}

	@Test
	void evaluateShouldThrowWhenGuessIsNull() {
		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.evaluate(List.of(1, 2, 3, 4), null));

		assertEquals("Não foi possível avaliar a tentativa", exception.getMessage());
	}

	@Test
	void evaluateShouldThrowWhenSecretAndGuessHaveDifferentSizes() {
		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameEngine.evaluate(List.of(1, 2, 3, 4), List.of(1, 2, 3)));

		assertEquals("Não foi possível avaliar a tentativa", exception.getMessage());
	}

	@Test
	void evaluateShouldReturnAllCorrectPositionsWhenGuessMatchesSecret() {
		GameEngineResult result = gameEngine.evaluate(List.of(1, 2, 3, 4), List.of(1, 2, 3, 4));

		assertEquals(4, result.getCorrectPositions());
		assertEquals(0, result.getCorrectColors());
	}

	@Test
	void evaluateShouldReturnCorrectPositionsAndCorrectColorsForMixedGuess() {
		GameEngineResult result = gameEngine.evaluate(List.of(1, 2, 3, 4), List.of(1, 4, 2, 6));

		assertEquals(1, result.getCorrectPositions());
		assertEquals(2, result.getCorrectColors());
	}

	@Test
	void evaluateShouldHandleDuplicateColorsWithoutDoubleCounting() {
		GameEngineResult result = gameEngine.evaluate(List.of(1, 1, 2, 3), List.of(1, 2, 1, 4));

		assertEquals(1, result.getCorrectPositions());
		assertEquals(2, result.getCorrectColors());
	}

	@Test
	void evaluateShouldReturnZeroWhenNoColorMatches() {
		GameEngineResult result = gameEngine.evaluate(List.of(1, 2, 3, 4), List.of(5, 6, 5, 6));

		assertEquals(0, result.getCorrectPositions());
		assertEquals(0, result.getCorrectColors());
	}

	// ===== isWinning =====
	@Test
	void isWinningShouldReturnTrueWhenCorrectPositionsEqualsCodeLength() {
		boolean won = gameEngine.isWinning(new GameEngineResult(4, 0), 4);

		assertTrue(won);
	}

	@Test
	void isWinningShouldReturnFalseWhenCorrectPositionsIsLessThanCodeLength() {
		boolean won = gameEngine.isWinning(new GameEngineResult(3, 1), 4);

		assertEquals(false, won);
	}

	private Game createGame(int codeLength, GameLevel level) {
		Game game = new Game();
		game.setCodeLength(codeLength);
		game.setLevel(level);
		return game;
	}
}
