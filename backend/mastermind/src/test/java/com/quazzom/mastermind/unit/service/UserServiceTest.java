package com.quazzom.mastermind.unit.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.quazzom.mastermind.dto.GameFullResponse;
import com.quazzom.mastermind.dto.GameHistoryResponse;
import com.quazzom.mastermind.dto.UserPasswordRequest;
import com.quazzom.mastermind.dto.UserProfileRequest;
import com.quazzom.mastermind.dto.UserProfileResponse;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.Guess;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.GameNotFoundException;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.GuessRepository;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.service.UserService;
import com.quazzom.mastermind.utils.SecretDecoder;
import com.quazzom.mastermind.validator.UserPasswordRequestValidator;
import com.quazzom.mastermind.validator.UserProfileRequestValidator;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GuessRepository guessRepository;

    @Mock
    private UserProfileRequestValidator userProfileRequestValidator;

    @Mock
    private UserPasswordRequestValidator userPasswordRequestValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecretDecoder secretDecoder;

    @InjectMocks
    private UserService userService;

    private UUID uuidPublic;
    private User user;

    @BeforeEach
    void setUp() {
        uuidPublic = UUID.randomUUID();
        user = new User();
        user.setId(10L);
        user.setUuidPublic(uuidPublic);
        user.setName("Maria Silva");
        user.setEmail("maria@teste.com");
        user.setNickname("maria");
        user.setAge(25);
        user.setPassword("encoded-password");
    }

    @Test
    void getProfileShouldReturnProfileData() {
        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));

        UserProfileResponse response = userService.getProfile(uuidPublic);

        assertEquals("Maria Silva", response.getName());
        assertEquals("maria@teste.com", response.getEmail());
        assertEquals("maria", response.getNickname());
        assertEquals(25, response.getAge());
    }

    @Test
    void updateProfileShouldUpdateAndReturnDataWhenNoConflict() {
        UserProfileRequest request = new UserProfileRequest();
        request.setName("Maria Souza");
        request.setNickname("marias");
        request.setAge(26);

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(userRepository.findByNickname(request.getNickname())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfileResponse response = userService.updateProfile(uuidPublic, request);

        verify(userProfileRequestValidator).validateRequestBody(request);
        assertEquals("Maria Souza", response.getName());
        assertEquals("marias", response.getNickname());
        assertEquals(26, response.getAge());
    }


    @Test
    void deleteProfileShouldDeleteAuthenticatedUser() {
        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));

        userService.deleteProfile(uuidPublic);

        verify(userRepository).delete(user);
    }

    @Test
    void updatePasswordShouldSaveEncodedPasswordWhenCurrentIsValid() {
        UserPasswordRequest request = new UserPasswordRequest();
        request.setCurrentPassword("Abc@123");
        request.setNewPassword("Def@456");

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Abc@123", "encoded-password")).thenReturn(true);
        when(passwordEncoder.encode("Def@456")).thenReturn("new-encoded-password");

        userService.updatePassword(uuidPublic, request);

        verify(userPasswordRequestValidator).validateRequestBody(request);
        verify(userRepository).save(user);
        assertEquals("new-encoded-password", user.getPassword());
    }

    @Test
    void updatePasswordShouldThrowWhenCurrentPasswordIsInvalid() {
        UserPasswordRequest request = new UserPasswordRequest();
        request.setCurrentPassword("wrong");
        request.setNewPassword("Def@456");

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> userService.updatePassword(uuidPublic, request));

        assertEquals("Senha atual inválida", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ===== getHistory() =====

    @Test
    void getHistoryShouldReturnBestGamesAndFullHistory() {

        Game wonGame = new Game();
        wonGame.setUuidPublic(UUID.randomUUID());
        wonGame.setLevel(GameLevel.EASY);
        wonGame.setStatus(GameStatus.WON);
        wonGame.setAttemptsUsed(3);
        wonGame.setCreatedAt(LocalDateTime.now().minusDays(1));
        wonGame.setFinishedAt(LocalDateTime.now());

        Game lostGame = new Game();
        lostGame.setUuidPublic(UUID.randomUUID());
        lostGame.setLevel(GameLevel.NORMAL);
        lostGame.setStatus(GameStatus.LOST);
        lostGame.setAttemptsUsed(10);
        lostGame.setCreatedAt(LocalDateTime.now().minusDays(2));
        lostGame.setFinishedAt(LocalDateTime.now().minusDays(1));

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(gameRepository.findBestGamesPerLevel(user.getId())).thenReturn(List.of(wonGame));
        when(gameRepository.findHistoryByUserId(user.getId())).thenReturn(List.of(wonGame, lostGame));

        GameHistoryResponse response = userService.getHistory(uuidPublic);

        assertEquals(1, response.getGameHistoryBestGames().size());
        assertEquals(2, response.getGameHistoryFull().size());
    }

    @Test
    void getHistoryShouldReturnPositivePointsForWonGame() {

        Game wonGame = new Game();
        wonGame.setUuidPublic(UUID.randomUUID());
        wonGame.setLevel(GameLevel.EASY);
        wonGame.setStatus(GameStatus.WON);
        wonGame.setAttemptsUsed(1);
        wonGame.setCreatedAt(LocalDateTime.now().minusDays(1));
        wonGame.setFinishedAt(LocalDateTime.now());

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(gameRepository.findBestGamesPerLevel(user.getId())).thenReturn(List.of(wonGame));
        when(gameRepository.findHistoryByUserId(user.getId())).thenReturn(List.of(wonGame));

        GameHistoryResponse response = userService.getHistory(uuidPublic);

        assertEquals(1, response.getGameHistoryFull().size());
        assertEquals(100, response.getGameHistoryFull().get(0).getPointsMaked());
    }

    @Test
    void getHistoryShouldReturnZeroPointsForLostGame() {
        Game lostGame = new Game();
        lostGame.setUuidPublic(UUID.randomUUID());
        lostGame.setLevel(GameLevel.EASY);
        lostGame.setStatus(GameStatus.LOST);
        lostGame.setAttemptsUsed(10);
        lostGame.setCreatedAt(LocalDateTime.now().minusDays(1));
        lostGame.setFinishedAt(LocalDateTime.now());

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(gameRepository.findBestGamesPerLevel(user.getId())).thenReturn(List.of());
        when(gameRepository.findHistoryByUserId(user.getId())).thenReturn(List.of(lostGame));

        GameHistoryResponse response = userService.getHistory(uuidPublic);

        assertEquals(0, response.getGameHistoryFull().get(0).getPointsMaked());
    }

    @Test
    void getHistoryShouldReturnEmptyListsWhenUserHasNoGames() {
        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(gameRepository.findBestGamesPerLevel(user.getId())).thenReturn(List.of());
        when(gameRepository.findHistoryByUserId(user.getId())).thenReturn(List.of());

        GameHistoryResponse response = userService.getHistory(uuidPublic);

        assertTrue(response.getGameHistoryBestGames().isEmpty());
        assertTrue(response.getGameHistoryFull().isEmpty());
    }

    @Test
    void getHistoryShouldThrowWhenUserIsNotAuthenticated() {
        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> userService.getHistory(uuidPublic));

        assertEquals("Usuário não autenticado", exception.getMessage());
    }

    @Test
    void getUserGameThatIsNotInProgressShouldReturnGameStatusWithRows() {
        UUID gameUuidPublic = UUID.randomUUID();

        Game game = new Game();
        game.setId(100L);
        game.setUuidPublic(gameUuidPublic);
        game.setLevel(GameLevel.HARD);
		game.setStatus(GameStatus.IN_PROGRESS);
        game.setCodeLength(6);
        game.setAllowDuplicates(false);

        Guess guess = new Guess();
        guess.setAttemptNumber(1);
        guess.setGuess("1,2,3,4,5,6");
        guess.setCorrectPositions(2);
        guess.setCorrectColors(1);

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(gameRepository.findByUserIdAndUuidPublicAndStatusNot(user.getId(), gameUuidPublic, GameStatus.IN_PROGRESS))
                .thenReturn(Optional.of(game));
        when(guessRepository.findByGameIdOrderByAttemptNumberAsc(game.getId())).thenReturn(List.of(guess));
        when(secretDecoder.decode("1,2,3,4,5,6")).thenReturn(List.of(1, 2, 3, 4, 5, 6));

        GameFullResponse response = userService.getUserGameThatIsNotInProgress(uuidPublic, gameUuidPublic);

        assertEquals(GameStatus.IN_PROGRESS, response.getStatus());
        assertEquals(GameLevel.HARD, response.getGameLevel());
        assertEquals(6, response.getNumberOfColumnColors());
        assertEquals(10, response.getMaximumOfattempts());
        assertEquals(1, response.getRows().size());
        assertEquals(List.of(1, 2, 3, 4, 5, 6), response.getRows().get(0).getGuess());
        assertEquals(2, response.getRows().get(0).getTips().getCorrectPositions());
        assertEquals(1, response.getRows().get(0).getTips().getCorrectColors());
    }

    @Test
    void getUserGameThatIsNotInProgressShouldThrowWhenGameNotFound() {
        UUID gameUuidPublic = UUID.randomUUID();

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.of(user));
        when(gameRepository.findByUserIdAndUuidPublicAndStatusNot(user.getId(), gameUuidPublic, GameStatus.IN_PROGRESS))
                .thenReturn(Optional.empty());

        GameNotFoundException exception = assertThrows(GameNotFoundException.class,
                () -> userService.getUserGameThatIsNotInProgress(uuidPublic, gameUuidPublic));

        assertEquals("O jogo não existe ou ainda está com o estado IN_PROGRESS", exception.getMessage());
    }

    @Test
    void getUserGameThatIsNotInProgressShouldThrowWhenUserIsNotAuthenticated() {
        UUID gameUuidPublic = UUID.randomUUID();

        when(userRepository.findByUuidPublic(uuidPublic)).thenReturn(Optional.empty());

        UnauthorizedException exception = assertThrows(UnauthorizedException.class,
                () -> userService.getUserGameThatIsNotInProgress(uuidPublic, gameUuidPublic));

        assertEquals("Usuário não autenticado", exception.getMessage());
    }

}