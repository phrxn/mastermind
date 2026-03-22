package com.quazzom.mastermind.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.quazzom.mastermind.businessrules.GameBusinessRole;
import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.dto.GameEndResponse;
import com.quazzom.mastermind.dto.GameInProgressResponse;
import com.quazzom.mastermind.dto.GameResponse;
import com.quazzom.mastermind.dto.GameStatusResponse;
import com.quazzom.mastermind.dto.GameStatusRowResponse;
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
import com.quazzom.mastermind.utils.SecretDecoder;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GuessRepository guessRepository;
    private final UserRepository userRepository;
    private final GameEngine gameEngine;

    private final GameBusinessRole gameBusinessRole;

    private final SecretDecoder secretDecoder;

    private final ConcurrentHashMap<Long, List<Integer>> inMemorySecretsByGameId = new ConcurrentHashMap<>();

    public GameService(
            GameRepository gameRepository,
            GuessRepository guessRepository,
            UserRepository userRepository,
            GameEngine gameEngine,
            GameBusinessRole gameBusinessRole,
            SecretDecoder secretDecoder) {
        this.gameRepository = gameRepository;
        this.guessRepository = guessRepository;
        this.userRepository = userRepository;
        this.gameEngine = gameEngine;
        this.gameBusinessRole = gameBusinessRole;
        this.secretDecoder = secretDecoder;
    }

    public GameStatusResponse createGame(Long userId, GameLevel level) {

        User user = findAuthenticatedUser(userId);

        // check if the 'level' value is valid
        gameBusinessRole.setLevel(level);

        Optional<Game> inProgress = gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), GameStatus.IN_PROGRESS);

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
        game.setSecretCode(secretDecoder.encode(secret));
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setAttemptsUsed(0);

        Game gameSaved = gameRepository.save(game);
        inMemorySecretsByGameId.put(gameSaved.getId(), new ArrayList<>(secret));

        return new GameStatusResponse(GameStatus.IN_PROGRESS,
                gameSaved.getLevel(),
                gameSaved.getCodeLength(),
                GameEngine.MAX_ATTEMPTS,
                gameSaved.getAllowDuplicates(),
                new ArrayList<>());
    }

    @Transactional
    public GameResponse makeGuess(Long userId, List<Integer> guessValues) {

        gameBusinessRole.setGuess(guessValues);

        Game game = getInProgressGame(userId);
        List<Integer> secret = getSecretInMemory(game);

        gameEngine.validateGuess(game, guessValues);

        GameEngineResult result = gameEngine.evaluate(secret, guessValues);

        int nextAttemptNumber = game.getAttemptsUsed() + 1;
        Guess guess = new Guess();
        guess.setGame(game);
        guess.setAttemptNumber(nextAttemptNumber);
        guess.setGuess(secretDecoder.encode(guessValues));
        guess.setCorrectPositions(result.getCorrectPositions());
        guess.setCorrectColors(result.getCorrectColors());
        guessRepository.save(guess);

        game.setAttemptsUsed(nextAttemptNumber);

        if (gameEngine.isWinning(result, game.getCodeLength())) {
            game.setStatus(GameStatus.WON);
            game.setFinishedAt(LocalDateTime.now());
            gameRepository.save(game);
            inMemorySecretsByGameId.remove(game.getId());

            return new GameEndResponse(GameStatus.WON, game.getLevel(), secret);
        }

        if (nextAttemptNumber >= GameEngine.MAX_ATTEMPTS) {
            game.setStatus(GameStatus.LOST);
            game.setFinishedAt(LocalDateTime.now());
            gameRepository.save(game);
            inMemorySecretsByGameId.remove(game.getId());

            return new GameEndResponse(GameStatus.LOST, game.getLevel(), secret);
        }

        gameRepository.save(game);
        return new GameInProgressResponse(GameStatus.IN_PROGRESS, game.getLevel(), result);
    }

    public GameEndResponse giveUp(Long userId) {

        Game game;

        try {
            game = getInProgressGame(userId);
        } catch (GameFlowException e) {
            throw new GameNotFoundException("Não existe nenhum jogo em andamento para desistir");
        }

        List<Integer> secret = getSecretInMemory(game);

        game.setStatus(GameStatus.GAVE_UP);
        game.setFinishedAt(LocalDateTime.now());
        gameRepository.save(game);
        inMemorySecretsByGameId.remove(game.getId());

        return new GameEndResponse(GameStatus.GAVE_UP, game.getLevel(), secret);
    }

    public Optional<GameStatusResponse> status(Long userId) {

        User user = findAuthenticatedUser(userId);
        Optional<Game> inProgress = gameRepository.findFirstByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), GameStatus.IN_PROGRESS);

        if (inProgress.isEmpty()) {
            return Optional.empty();
        }

        Game game = inProgress.get();

        List<Guess> guesses = guessRepository.findByGameIdOrderByAttemptNumberAsc(game.getId());
        List<GameStatusRowResponse> rows = guesses.stream()
                .map(item
                        -> new GameStatusRowResponse(
                        secretDecoder.decode(item.getGuess()),
                        new GameEngineResult(item.getCorrectPositions(), item.getCorrectColors())
                ))
                .collect(Collectors.toList());

        GameStatusResponse response = new GameStatusResponse(
                GameStatus.IN_PROGRESS,
                game.getLevel(),
                game.getCodeLength(),
                GameEngine.MAX_ATTEMPTS,
                game.getAllowDuplicates(),
                rows);

        return Optional.of(response);
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

    private LevelConfig levelConfig(GameLevel level) {

        if (level == null) {
            throw new GameFlowException("Nível é obrigatório");
        }

        return switch (level) {
            case EASY ->
                new LevelConfig(GameLevel.EASY, 4, false);
            case NORMAL ->
                new LevelConfig(GameLevel.NORMAL, 4, true);
            case HARD ->
                new LevelConfig(GameLevel.HARD, 6, false);
            case MASTERMIND ->
                new LevelConfig(GameLevel.MASTERMIND, 6, true);
            default ->
                throw new GameFlowException("Nível inválido. Use EASY, NORMAL, HARD ou MASTERMIND");
        };
    }

    private List<Integer> getSecretInMemory(Game game) {
        return inMemorySecretsByGameId.computeIfAbsent(game.getId(), ignored -> secretDecoder.decode(game.getSecretCode()));
    }

    private record LevelConfig(GameLevel level, int codeLength, boolean allowDuplicates) {

    }
}
