package com.quazzom.mastermind.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.dto.GameFullResponse;
import com.quazzom.mastermind.dto.GameHistoryItemResponse;
import com.quazzom.mastermind.dto.GameHistoryResponse;
import com.quazzom.mastermind.dto.GameStatusRowResponse;
import com.quazzom.mastermind.dto.UserPasswordRequest;
import com.quazzom.mastermind.dto.UserProfileRequest;
import com.quazzom.mastermind.dto.UserProfileResponse;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.Guess;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.GameNotFoundException;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.exception.UserAlreadyExistsException;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.GuessRepository;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.utils.CreateGameHistoryItemResponse;
import com.quazzom.mastermind.utils.SecretDecoder;
import com.quazzom.mastermind.validator.UserPasswordRequestValidator;
import com.quazzom.mastermind.validator.UserProfileRequestValidator;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final GuessRepository guessRepository;
    private final UserProfileRequestValidator userProfileRequestValidator;
    private final UserPasswordRequestValidator userPasswordRequestValidator;
    private final PasswordEncoder passwordEncoder;

    private final SecretDecoder secretDecoder;

	private CreateGameHistoryItemResponse calcGamePoints;

    public UserService(UserRepository userRepository,
            GameRepository gameRepository,
            GuessRepository guessRepository,
            UserProfileRequestValidator userProfileRequestValidator,
            UserPasswordRequestValidator userPasswordRequestValidator,
            PasswordEncoder passwordEncoder,
            SecretDecoder secretDecoder,
            CreateGameHistoryItemResponse calcGamePoints) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.guessRepository = guessRepository;
        this.userProfileRequestValidator = userProfileRequestValidator;
        this.userPasswordRequestValidator = userPasswordRequestValidator;
        this.passwordEncoder = passwordEncoder;
        this.secretDecoder = secretDecoder;
		this.calcGamePoints = calcGamePoints;
    }

    public UserProfileResponse getProfile(UUID uuidPublic) {
        User user = getAuthenticatedUser(uuidPublic);
        return new UserProfileResponse(user);
    }

    public UserProfileResponse updateProfile(UUID uuidPublic, UserProfileRequest request) {

        userProfileRequestValidator.validateRequestBody(request);

        User user = getAuthenticatedUser(uuidPublic);

        userRepository.findByNickname(request.getNickname())
                .filter(foundUser -> !foundUser.getUuidPublic().equals(uuidPublic))
                .ifPresent(foundUser -> {
                    throw new UserAlreadyExistsException("O nickname já está em uso");
                });

        user.setName(request.getName());
        user.setNickname(request.getNickname());
        user.setAge(request.getAge());

        User updatedUser = userRepository.save(user);
        return new UserProfileResponse(updatedUser);
    }

    public void deleteProfile(UUID uuidPublic) {
        User user = getAuthenticatedUser(uuidPublic);
        userRepository.delete(user);
    }

    public void updatePassword(UUID uuidPublic, UserPasswordRequest request) {
        userPasswordRequestValidator.validateRequestBody(request);

        User user = getAuthenticatedUser(uuidPublic);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new UnauthorizedException("Senha atual inválida");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    public GameHistoryResponse getHistory(UUID uuidPublic) {

        User user = getAuthenticatedUser(uuidPublic);

        List<Game> userTopGamesByLevel = gameRepository.findBestGamesPerLevel(user.getId());
        List<Game> userGameHistory = gameRepository.findHistoryByUserId(user.getId());

        calcGamePoints = new CreateGameHistoryItemResponse();

        List<GameHistoryItemResponse> gameHistoryBestGames = userTopGamesByLevel.stream()
                .map(game -> calcGamePoints.calculatePoints(game))
                .toList();

        List<GameHistoryItemResponse> gameHistoryFull = userGameHistory.stream()
                .map(game -> calcGamePoints.calculatePoints(game))
                .toList();

        return new GameHistoryResponse(gameHistoryBestGames, gameHistoryFull);

    }

    /**
     * Returns the status of a specific user's game, provided that game is no
     * longer in progress (it could be a won, lost, or abandoned game)
     *
     * @param userUuidPublic the userId of the authenticated user
     * @param gameUuidPublic the game public uuid
     *
     * @return an Optional containing the GameStatusResponse if the game is not
     * in progress, or an empty Optional if the game is in progress or not found
     */
    public GameFullResponse getUserGameThatIsNotInProgress(UUID userUuidPublic, UUID gameUuidPublic) {

        User user = getAuthenticatedUser(userUuidPublic);

        Optional<Game> theUserGameNotInProgress = gameRepository.findByUserIdAndUuidPublicAndStatusNot(user.getId(), gameUuidPublic, GameStatus.IN_PROGRESS);

        if (theUserGameNotInProgress.isEmpty()) {
            throw new GameNotFoundException("O jogo não existe ou ainda está com o estado IN_PROGRESS");
        }

        Game game = theUserGameNotInProgress.get();

        List<Guess> guesses = guessRepository.findByGameIdOrderByAttemptNumberAsc(game.getId());
        List<GameStatusRowResponse> rows = guesses.stream()
                .map(item
                        -> new GameStatusRowResponse(
                        secretDecoder.decode(item.getGuess()),
                        new GameEngineResult(item.getCorrectPositions(), item.getCorrectColors())
                ))
                .collect(Collectors.toList());

        GameFullResponse response = new GameFullResponse(
                game.getStatus(),
                game.getLevel(),
                game.getCodeLength(),
                GameEngine.MAX_ATTEMPTS,
                game.getAllowDuplicates(),
                secretDecoder.decode(game.getSecretCode()),
                rows);

        return response;
    }

    private User getAuthenticatedUser(UUID uuidPublic) {
        return userRepository.findByUuidPublic(uuidPublic)
                .orElseThrow(() -> new UnauthorizedException("Usuário não autenticado"));
    }
}
