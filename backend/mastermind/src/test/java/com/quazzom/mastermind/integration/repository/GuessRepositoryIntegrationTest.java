package com.quazzom.mastermind.integration.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.Guess;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.GuessRepository;
import com.quazzom.mastermind.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
public class GuessRepositoryIntegrationTest {

    @Autowired
    private GuessRepository guessRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        guessRepository.deleteAll();
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveGuessAndFindByGameIdOrderedByAttemptNumber() {
        User user = userRepository.save(buildValidUser("player1@email.com", "player1"));
        Game game = gameRepository.save(buildValidGame(user));

        Guess secondAttempt = buildGuess(game, 2, "RBGY", 2, 1);
        Guess firstAttempt = buildGuess(game, 1, "RGBY", 1, 2);
        guessRepository.save(secondAttempt);
        guessRepository.save(firstAttempt);

        List<Guess> guesses = guessRepository.findByGameIdOrderByAttemptNumberAsc(game.getId());

        assertEquals(2, guesses.size());
        assertEquals(1, guesses.get(0).getAttemptNumber());
        assertEquals(2, guesses.get(1).getAttemptNumber());
    }

    @Test
    void shouldPersistAllExplicitGuessProperties() {
        User user = userRepository.save(buildValidUser("player2@email.com", "player2"));
        Game game = gameRepository.save(buildValidGame(user));

        Guess guess = new Guess();
        guess.setGame(game);
        guess.setAttemptNumber(3);
        guess.setGuess("RRGG");
        guess.setCorrectPositions(2);
        guess.setCorrectColors(1);
        LocalDateTime createdAt = LocalDateTime.now().minusMinutes(5);
        guess.setCreatedAt(createdAt);

        Guess saved = guessRepository.save(guess);

        assertNotNull(saved.getId());
        assertEquals(game.getId(), saved.getGame().getId());
        assertEquals(3, saved.getAttemptNumber());
        assertEquals("RRGG", saved.getGuess());
        assertEquals(2, saved.getCorrectPositions());
        assertEquals(1, saved.getCorrectColors());
        assertEquals(createdAt, saved.getCreatedAt());
    }

    private User buildValidUser(String email, String nickname) {
        User user = new User();
        user.setName("Jogador Teste");
        user.setEmail(email);
        user.setNickname(nickname);
        user.setAge(25);
        user.setPassword("senha");
        return user;
    }

    private Game buildValidGame(User user) {
        Game game = new Game();
        game.setUser(user);
        game.setLevel(GameLevel.NORMAL);
        game.setCodeLength(4);
        game.setAllowDuplicates(false);
        game.setSecretCode("RGBY");
        game.setStatus(GameStatus.IN_PROGRESS);
        return game;
    }

    private Guess buildGuess(Game game, Integer attemptNumber, String guessValue, Integer correctPositions, Integer correctColors) {
        Guess guess = new Guess();
        guess.setGame(game);
        guess.setAttemptNumber(attemptNumber);
        guess.setGuess(guessValue);
        guess.setCorrectPositions(correctPositions);
        guess.setCorrectColors(correctColors);
        return guess;
    }

}
