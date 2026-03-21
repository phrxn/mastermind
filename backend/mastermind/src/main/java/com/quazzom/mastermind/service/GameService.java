package com.quazzom.mastermind.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.dto.GameCreateResponse;
import com.quazzom.mastermind.dto.GameGiveUpResponse;
import com.quazzom.mastermind.dto.GameGuessResponse;
import com.quazzom.mastermind.dto.GameStatusColumnResponse;
import com.quazzom.mastermind.dto.GameStatusResponse;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.Guess;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.GameFlowException;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.GuessRepository;
import com.quazzom.mastermind.repository.UserRepository;

@Service
public class GameService {

	private final GameRepository gameRepository;
	private final GuessRepository guessRepository;
	private final UserRepository userRepository;
	private final GameEngine gameEngine;

	private final ConcurrentHashMap<Long, List<Integer>> inMemorySecretsByGameId = new ConcurrentHashMap<>();

	public GameService(
			GameRepository gameRepository,
			GuessRepository guessRepository,
			UserRepository userRepository,
			GameEngine gameEngine) {
		this.gameRepository = gameRepository;
		this.guessRepository = guessRepository;
		this.userRepository = userRepository;
		this.gameEngine = gameEngine;
	}

	public GameCreateResponse createGame(Long userId, Integer level) {
		User user = findAuthenticatedUser(userId);

		Optional<Game> inProgress = gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(
				user.getId(), GameStatus.IN_PROGRESS);

		if (inProgress.isPresent()) {
			throw new GameFlowException(
					"Já existe um jogo em andamento, não é possível iniciar um novo enquanto houver outro em andamento");
		}

		LevelConfig config = levelConfig(level);
		List<Integer> secret = gameEngine.createSecret(config.codeLength(), config.allowDuplicates());

		Game game = new Game();
		game.setUser(user);
		game.setLevel(config.level());
		game.setCodeLength(config.codeLength());
		game.setAllowDuplicates(config.allowDuplicates());
		game.setSecretCode(encode(secret));
		game.setStatus(GameStatus.IN_PROGRESS);
		game.setAttemptsUsed(0);
		game.setScore(0);

		Game saved = gameRepository.save(game);
		inMemorySecretsByGameId.put(saved.getId(), new ArrayList<>(secret));

		return new GameCreateResponse("GAME_IN_PROGRESS", levelFromEnum(saved.getLevel()));
	}

	public GameGuessResponse makeGuess(Long userId, List<Integer> guessValues) {

		Game game = getInProgressGame(userId);
		List<Integer> secret = getSecretInMemoryOrDatabase(game);

		gameEngine.validateGuess(guessValues, game.getCodeLength());

		GameEngineResult result = gameEngine.evaluate(secret, guessValues);

		int nextAttemptNumber = game.getAttemptsUsed() + 1;
		Guess guess = new Guess();
		guess.setGame(game);
		guess.setAttemptNumber(nextAttemptNumber);
		guess.setGuess(encode(guessValues));
		guess.setCorrectPositions(result.getCorrectPositions());
		guess.setCorrectColors(result.getCorrectColors());
		guessRepository.save(guess);

		game.setAttemptsUsed(nextAttemptNumber);

		if (gameEngine.isWinning(result, game.getCodeLength())) {
			game.setStatus(GameStatus.WON);
			game.setFinishedAt(LocalDateTime.now());
			game.setScore(calculateScore(nextAttemptNumber));
			gameRepository.save(game);
			inMemorySecretsByGameId.remove(game.getId());

			return new GameGuessResponse("GAME_WIN", levelFromEnum(game.getLevel()), null, secret);
		}

		if (nextAttemptNumber >= GameEngine.MAX_ATTEMPTS) {
			game.setStatus(GameStatus.LOST);
			game.setFinishedAt(LocalDateTime.now());
			game.setScore(0);
			gameRepository.save(game);
			inMemorySecretsByGameId.remove(game.getId());

			return new GameGuessResponse("GAME_OVER", levelFromEnum(game.getLevel()), null, secret);
		}

		gameRepository.save(game);
		return new GameGuessResponse("GAME_IN_PROGRESS", levelFromEnum(game.getLevel()), result.getTips(), null);
	}

	public GameGiveUpResponse giveUp(Long userId) {
		Game game = getInProgressGame(userId);
		List<Integer> secret = getSecretInMemoryOrDatabase(game);

		game.setStatus(GameStatus.GAVE_UP);
		game.setFinishedAt(LocalDateTime.now());
		gameRepository.save(game);
		inMemorySecretsByGameId.remove(game.getId());

		return new GameGiveUpResponse("GAME_GIVE_UP", levelFromEnum(game.getLevel()), secret);
	}

	public Optional<GameStatusResponse> status(Long userId) {
		User user = findAuthenticatedUser(userId);
		Optional<Game> inProgress = gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(
				user.getId(), GameStatus.IN_PROGRESS);

		if (inProgress.isEmpty()) {
			return Optional.empty();
		}

		Game game = inProgress.get();
		getSecretInMemoryOrDatabase(game);

		List<Guess> guesses = guessRepository.findByGameIdOrderByAttemptNumberAsc(game.getId());
		List<GameStatusColumnResponse> columns = guesses.stream()
				.map(item -> new GameStatusColumnResponse(
						decode(item.getGuess()),
						gameEngine.buildShuffledTips(item.getCorrectPositions(), item.getCorrectColors())))
				.collect(Collectors.toList());

		GameStatusResponse response = new GameStatusResponse(
				"GAME_IN_PROGRESS",
				levelFromEnum(game.getLevel()),
				columns);

		return Optional.of(response);
	}

	private int calculateScore(int attemptsUsed) {
		int remaining = Math.max(0, GameEngine.MAX_ATTEMPTS - attemptsUsed + 1);
		return remaining * 100;
	}

	private User findAuthenticatedUser(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));
	}

	private Game getInProgressGame(Long userId) {
		User user = findAuthenticatedUser(userId);
		return gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), GameStatus.IN_PROGRESS)
				.orElseThrow(() -> new GameFlowException(
						"Não existe nenhum jogo em andamento, não é possível fazer essa operação"));
	}

	private LevelConfig levelConfig(Integer level) {
		if (level == null) {
			throw new GameFlowException("Nível é obrigatório");
		}

		return switch (level) {
			case 1 -> new LevelConfig(GameLevel.EASY, 4, false);
			case 2 -> new LevelConfig(GameLevel.NORMAL, 4, true);
			case 3 -> new LevelConfig(GameLevel.HARD, 6, false);
			case 4 -> new LevelConfig(GameLevel.MASTERMIND, 6, true);
			default -> throw new GameFlowException("Nível inválido. Use 1, 2, 3 ou 4");
		};
	}

	private int levelFromEnum(GameLevel level) {
		return switch (level) {
			case EASY -> 1;
			case NORMAL -> 2;
			case HARD -> 3;
			case MASTERMIND -> 4;
		};
	}

	private List<Integer> getSecretInMemoryOrDatabase(Game game) {
		return inMemorySecretsByGameId.computeIfAbsent(game.getId(), ignored -> decode(game.getSecretCode()));
	}

	private String encode(List<Integer> values) {
		return values.stream().map(String::valueOf).collect(Collectors.joining(","));
	}

	private List<Integer> decode(String value) {
		if (value == null || value.isBlank()) {
			return List.of();
		}

		return Arrays.stream(value.split(","))
				.map(String::trim)
				.filter(item -> !item.isBlank())
				.map(Integer::parseInt)
				.collect(Collectors.toList());
	}

	private record LevelConfig(GameLevel level, int codeLength, boolean allowDuplicates) {
	}
}
