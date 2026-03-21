package com.quazzom.mastermind.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.dto.GameCreateResponse;
import com.quazzom.mastermind.dto.GameGiveUpResponse;
import com.quazzom.mastermind.dto.GameGuessResponse;
import com.quazzom.mastermind.dto.GameStatusResponse;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.GameFlowException;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.GuessRepository;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.service.GameService;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

	@Mock
	private GameRepository gameRepository;

	@Mock
	private GuessRepository guessRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private GameEngine gameEngine;

	@InjectMocks
	private GameService gameService;

	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(10L);
		user.setEmail("player@email.com");
	}

	@Test
	void createGameShouldThrowWhenAlreadyInProgress() {
		Game inProgress = new Game();
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(inProgress));

		assertThrows(GameFlowException.class, () -> gameService.createGame(10L, 1));
		verify(gameRepository, never()).save(any(Game.class));
	}

	@Test
	void createGameShouldPersistAndReturnInProgress() {
		Game saved = new Game();
		saved.setId(90L);
		saved.setLevel(GameLevel.EASY);
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.empty());
		when(gameEngine.createSecret(4, false)).thenReturn(List.of(1, 2, 3, 4));
		when(gameRepository.save(any(Game.class))).thenReturn(saved);

		GameCreateResponse response = gameService.createGame(10L, 1);

		assertEquals("GAME_IN_PROGRESS", response.getStatus());
		assertEquals(1, response.getGameLevel());
	}

	@Test
	void makeGuessShouldReturnGameWinWhenAllPositionsMatch() {
		Game game = new Game();
		game.setId(7L);
		game.setLevel(GameLevel.EASY);
		game.setCodeLength(4);
		game.setSecretCode("1,2,3,4");
		game.setStatus(GameStatus.IN_PROGRESS);
		game.setAttemptsUsed(0);

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(game));
		when(gameEngine.isWinning(any(GameEngineResult.class), org.mockito.ArgumentMatchers.eq(4))).thenReturn(true);
		when(gameEngine.evaluate(List.of(1, 2, 3, 4), List.of(1, 2, 3, 4)))
				.thenReturn(new GameEngineResult(4, 0, List.of(1, 1, 1, 1)));

		GameGuessResponse response = gameService.makeGuess(10L, List.of(1, 2, 3, 4));

		assertEquals("GAME_WIN", response.getStatus());
		assertEquals(List.of(1, 2, 3, 4), response.getSecret());
		verify(guessRepository).save(any());
		verify(gameRepository).save(any(Game.class));
	}

	@Test
	void giveUpShouldCloseGameAndReturnSecret() {
		Game game = new Game();
		game.setId(7L);
		game.setLevel(GameLevel.NORMAL);
		game.setCodeLength(4);
		game.setSecretCode("6,5,4,3");
		game.setStatus(GameStatus.IN_PROGRESS);

		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(game));

		GameGiveUpResponse response = gameService.giveUp(10L);

		assertEquals("GAME_GIVE_UP", response.getStatus());
		assertEquals(List.of(6, 5, 4, 3), response.getSecret());
		verify(gameRepository).save(any(Game.class));
	}

	@Test
	void statusShouldReturnEmptyWhenNoInProgressGameExists() {
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.empty());

		Optional<GameStatusResponse> result = gameService.status(10L);

		assertTrue(result.isEmpty());
	}

	@Test
	void makeGuessShouldThrowWhenNoInProgressGameExists() {
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.empty());

		assertThrows(GameFlowException.class, () -> gameService.makeGuess(10L, List.of(1, 2, 3, 4)));
		verify(guessRepository, never()).save(any());
	}
}
