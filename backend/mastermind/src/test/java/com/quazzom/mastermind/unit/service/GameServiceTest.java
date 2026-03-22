package com.quazzom.mastermind.unit.service;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.quazzom.mastermind.businessrules.GameBusinessRole;
import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.dto.GameEndResponse;
import com.quazzom.mastermind.dto.GameInProgressResponse;
import com.quazzom.mastermind.dto.GameResponse;
import com.quazzom.mastermind.dto.GameStatusResponse;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.Guess;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.GameFlowException;
import com.quazzom.mastermind.exception.GameNotFoundException;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.GuessRepository;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.service.GameService;
import com.quazzom.mastermind.utils.SecretDecoder;

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

	@Mock
	private GameBusinessRole gameBusinessRole;

	@InjectMocks
	private GameService gameService;

	@Mock
	private SecretDecoder secretDecoder;

	@Captor
	private ArgumentCaptor<Game> gameCaptor;

	@Captor
	private ArgumentCaptor<Guess> guessCaptor;

	private User user;
	private Game inProgressGame;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(10L);
		user.setEmail("test@email.com");
		user.setNickname("test");

		inProgressGame = new Game();
		inProgressGame.setId(100L);
		inProgressGame.setUser(user);
		inProgressGame.setLevel(GameLevel.EASY);
		inProgressGame.setCodeLength(4);
		inProgressGame.setAllowDuplicates(false);
		inProgressGame.setSecretCode("1,2,3,4");
		inProgressGame.setStatus(GameStatus.IN_PROGRESS);
		inProgressGame.setAttemptsUsed(0);
	}

	// ===== createGame =====
	@Test
	void createGameShouldCreateAndReturnGameInProgress() {

		when(secretDecoder.encode(anyList())).thenReturn("1,2,3,4");
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.empty());
		when(gameEngine.createSecret(4, false)).thenReturn(List.of(1, 2, 3, 4));
		when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> {
			Game gameToSave = invocation.getArgument(0);
			gameToSave.setId(999L);
			return gameToSave;
		});

		GameStatusResponse response = gameService.createGame(10L, GameLevel.EASY);

		verify(gameBusinessRole).setLevel(GameLevel.EASY);
		verify(gameEngine).createSecret(4, false);
		verify(gameRepository).save(gameCaptor.capture());

		Game savedGame = gameCaptor.getValue();
		assertEquals(user, savedGame.getUser());
		assertEquals(GameLevel.EASY, savedGame.getLevel());
		assertEquals(4, savedGame.getCodeLength());
		assertEquals(false, savedGame.getAllowDuplicates());
		assertEquals("1,2,3,4", savedGame.getSecretCode());
		assertEquals(GameStatus.IN_PROGRESS, savedGame.getStatus());
		assertEquals(0, savedGame.getAttemptsUsed());

		assertEquals(GameStatus.IN_PROGRESS, response.getStatus());
		assertEquals(GameLevel.EASY, response.getGameLevel());
		assertEquals(4, response.getNumberOfColumnColors());
		assertEquals(GameEngine.MAX_ATTEMPTS, response.getMaximumOfattempts());
		assertEquals(false, response.isRepeatedColorAllowed());
		assertTrue(response.getRows().isEmpty());
	}

	@Test
	void createGameShouldThrowWhenUserIsNotAuthenticated() {
		when(userRepository.findById(10L)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> gameService.createGame(10L, GameLevel.EASY));

		assertEquals("Usuário não autenticado", exception.getMessage());
	}

	@Test
	void createGameShouldThrowWhenAlreadyExistsGameInProgress() {
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(inProgressGame));

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameService.createGame(10L, GameLevel.EASY));

		assertEquals("Já existe um jogo em andamento, não é possível iniciar um novo enquanto houver outro em andamento",
				exception.getMessage());
	}

	// ===== makeGuess =====
	@Test
	void makeGuessShouldReturnGameInProgressWhenNotWinningAndAttemptsRemain() {
		List<Integer> guessValues = List.of(1, 2, 4, 6);
		GameEngineResult result = new GameEngineResult(2, 1);

		when(secretDecoder.decode("1,2,3,4")).thenReturn(List.of(1, 2, 3, 4));
		when(secretDecoder.encode(guessValues)).thenReturn("1,2,4,6");
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(inProgressGame));
		when(gameEngine.evaluate(List.of(1, 2, 3, 4), guessValues)).thenReturn(result);
		when(gameEngine.isWinning(result, 4)).thenReturn(false);

		GameResponse response = gameService.makeGuess(10L, guessValues);

		assertInstanceOf(GameInProgressResponse.class, response);
		GameInProgressResponse inProgressResponse = (GameInProgressResponse) response;
		assertEquals(GameStatus.IN_PROGRESS, inProgressResponse.getStatus());
		assertEquals(GameLevel.EASY, inProgressResponse.getGameLevel());
		assertEquals(2, inProgressResponse.getTips().getCorrectPositions());
		assertEquals(1, inProgressResponse.getTips().getCorrectColors());

		verify(gameBusinessRole).setGuess(guessValues);
		verify(gameEngine).validateGuess(inProgressGame, guessValues);
		verify(guessRepository).save(guessCaptor.capture());
		verify(gameRepository).save(inProgressGame);

		Guess savedGuess = guessCaptor.getValue();
		assertEquals(1, savedGuess.getAttemptNumber());
		assertEquals("1,2,4,6", savedGuess.getGuess());
		assertEquals(2, savedGuess.getCorrectPositions());
		assertEquals(1, savedGuess.getCorrectColors());
		assertEquals(1, inProgressGame.getAttemptsUsed());
	}

	@Test
	void makeGuessShouldReturnGameWinWhenGuessMatchesSecret() {
		List<Integer> guessValues = List.of(1, 2, 3, 4);
		GameEngineResult result = new GameEngineResult(4, 0);

		when(secretDecoder.decode("1,2,3,4")).thenReturn(List.of(1, 2, 3, 4));
		when(secretDecoder.encode(anyList())).thenReturn("1,2,3,4");
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(inProgressGame));
		when(gameEngine.evaluate(List.of(1, 2, 3, 4), guessValues)).thenReturn(result);
		when(gameEngine.isWinning(result, 4)).thenReturn(true);

		GameResponse response = gameService.makeGuess(10L, guessValues);

		assertInstanceOf(GameEndResponse.class, response);
		GameEndResponse endResponse = (GameEndResponse) response;
		assertEquals(GameStatus.WON, endResponse.getStatus());
		assertEquals(GameLevel.EASY, endResponse.getGameLevel());
		assertEquals(List.of(1, 2, 3, 4), endResponse.getSecret());
		assertEquals(GameStatus.WON, inProgressGame.getStatus());
		assertNotNull(inProgressGame.getFinishedAt());
	}

	@Test
	void makeGuessShouldReturnGameOverWhenMaxAttemptsIsReached() {
		inProgressGame.setAttemptsUsed(9);
		List<Integer> guessValues = List.of(6, 5, 4, 3);
		GameEngineResult result = new GameEngineResult(0, 2);

		when(secretDecoder.decode("1,2,3,4")).thenReturn(List.of(1, 2, 3, 4));
		when(secretDecoder.encode(anyList())).thenReturn("6,5,4,3");
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(inProgressGame));
		when(gameEngine.evaluate(List.of(1, 2, 3, 4), guessValues)).thenReturn(result);
		when(gameEngine.isWinning(result, 4)).thenReturn(false);

		GameResponse response = gameService.makeGuess(10L, guessValues);

		assertInstanceOf(GameEndResponse.class, response);
		GameEndResponse endResponse = (GameEndResponse) response;
		assertEquals(GameStatus.LOST, endResponse.getStatus());
		assertEquals(GameLevel.EASY, endResponse.getGameLevel());
		assertEquals(List.of(1, 2, 3, 4), endResponse.getSecret());
		assertEquals(GameStatus.LOST, inProgressGame.getStatus());
		assertNotNull(inProgressGame.getFinishedAt());
		assertEquals(10, inProgressGame.getAttemptsUsed());
	}

	@Test
	void makeGuessShouldThrowWhenUserIsNotAuthenticated() {
		when(userRepository.findById(10L)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> gameService.makeGuess(10L, List.of(1, 2, 3, 4)));

		assertEquals("Usuário não autenticado", exception.getMessage());
	}

	@Test
	void makeGuessShouldThrowWhenNoGameInProgress() {
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.empty());

		GameFlowException exception = assertThrows(GameFlowException.class,
				() -> gameService.makeGuess(10L, List.of(1, 2, 3, 4)));

		assertEquals("Não existe nenhum jogo em andamento, não é possível fazer essa operação", exception.getMessage());
	}

	// ===== giveUp =====
	@Test
	void giveUpShouldEndGameWithGaveUpStatus() {
		when(secretDecoder.decode("1,2,3,4")).thenReturn(List.of(1, 2, 3, 4));
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(inProgressGame));
		when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

		GameEndResponse response = gameService.giveUp(10L);

		assertEquals(GameStatus.GAVE_UP, response.getStatus());
		assertEquals(GameLevel.EASY, response.getGameLevel());
		assertEquals(List.of(1, 2, 3, 4), response.getSecret());
		assertEquals(GameStatus.GAVE_UP, inProgressGame.getStatus());
		assertNotNull(inProgressGame.getFinishedAt());
		verify(gameRepository).save(inProgressGame);
	}

	@Test
	void giveUpShouldThrowWhenNoGameInProgress() {
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.empty());

		GameNotFoundException exception = assertThrows(GameNotFoundException.class,
				() -> gameService.giveUp(10L));

		assertEquals("Não existe nenhum jogo em andamento para desistir", exception.getMessage());
	}

	// ===== status =====
	@Test
	void statusShouldReturnEmptyWhenNoGameInProgress() {
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.empty());

		Optional<GameStatusResponse> response = gameService.status(10L);

		assertTrue(response.isEmpty());
	}

	@Test
	void statusShouldReturnRowsWhenGameIsInProgress() {
		Guess guess1 = new Guess();
		guess1.setAttemptNumber(1);
		guess1.setGuess("1,2,4,6");
		guess1.setCorrectPositions(2);
		guess1.setCorrectColors(1);

		Guess guess2 = new Guess();
		guess2.setAttemptNumber(2);
		guess2.setGuess("1,2,3,4");
		guess2.setCorrectPositions(4);
		guess2.setCorrectColors(0);

		when(secretDecoder.decode("1,2,4,6")).thenReturn(List.of(1, 2, 4, 6));
		when(secretDecoder.decode("1,2,3,4")).thenReturn(List.of(1, 2, 3, 4));
		when(userRepository.findById(10L)).thenReturn(Optional.of(user));
		when(gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(10L, GameStatus.IN_PROGRESS))
				.thenReturn(Optional.of(inProgressGame));
		when(guessRepository.findByGameIdOrderByAttemptNumberAsc(100L)).thenReturn(List.of(guess1, guess2));

		Optional<GameStatusResponse> responseOpt = gameService.status(10L);

		assertTrue(responseOpt.isPresent());
		GameStatusResponse response = responseOpt.get();
		assertEquals(GameStatus.IN_PROGRESS, response.getStatus());
		assertEquals(GameLevel.EASY, response.getGameLevel());
		assertEquals(4, response.getNumberOfColumnColors());
		assertEquals(GameEngine.MAX_ATTEMPTS, response.getMaximumOfattempts());
		assertEquals(false, response.isRepeatedColorAllowed());
		assertEquals(2, response.getRows().size());
		assertEquals(List.of(1, 2, 4, 6), response.getRows().get(0).getGuess());
		assertEquals(2, response.getRows().get(0).getTips().getCorrectPositions());
		assertEquals(1, response.getRows().get(0).getTips().getCorrectColors());
	}

	@Test
	void statusShouldThrowWhenUserIsNotAuthenticated() {
		when(userRepository.findById(10L)).thenReturn(Optional.empty());

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> gameService.status(10L));

		assertEquals("Usuário não autenticado", exception.getMessage());
	}

}
