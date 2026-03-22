package com.quazzom.mastermind.integration.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.UserRepository;

@SpringBootTest
@ActiveProfiles("test")
public class GameRepositoryIntegrationTest {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        gameRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldSaveGameAndFindByUserId() {
        User user = userRepository.save(buildValidUser("gamer1@email.com", "gamer1"));
        Game game = createGame(user);

        gameRepository.save(game);

        List<Game> games = gameRepository.findByUserId(user.getId());

        assertEquals(1, games.size());
        assertEquals(GameLevel.EASY, games.get(0).getLevel());
        assertEquals(GameStatus.IN_PROGRESS, games.get(0).getStatus());
    }

    @Test
    void shouldPersistAllExplicitGameProperties() {
        User user = userRepository.save(buildValidUser("gamer2@email.com", "gamer2"));
        Game game = new Game();
        game.setUser(user);
        game.setLevel(GameLevel.MASTERMIND);
        game.setCodeLength(6);
        game.setAllowDuplicates(true);
        game.setSecretCode("RRGGBB");
        game.setStatus(GameStatus.WON);
        game.setAttemptsUsed(4);
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        LocalDateTime finishedAt = LocalDateTime.now();
        game.setCreatedAt(createdAt);
        game.setFinishedAt(finishedAt);

        Game saved = gameRepository.save(game);

        assertNotNull(saved.getId());
        assertEquals(user.getId(), saved.getUser().getId());
        assertEquals(GameLevel.MASTERMIND, saved.getLevel());
        assertEquals(6, saved.getCodeLength());
        assertTrue(saved.getAllowDuplicates());
        assertEquals("RRGGBB", saved.getSecretCode());
        assertEquals(GameStatus.WON, saved.getStatus());
        assertEquals(4, saved.getAttemptsUsed());
        assertEquals(createdAt, saved.getCreatedAt());
        assertEquals(finishedAt, saved.getFinishedAt());
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

    @Test
    void shouldFindBestGamesPerLevelToUser_onlyWonGames() {

        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@email.com");
        user1.setNickname("user1");
        user1.setAge(25);
        user1.setPassword("123");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@email.com");
        user2.setNickname("user2");
        user2.setAge(30);
        user2.setPassword("123");
        userRepository.save(user2);

        LocalDateTime base = LocalDateTime.now();


        // easy level
							gameRepository.save(createGame(user1, GameLevel.EASY, 1, GameStatus.LOST, base.minusMinutes(6)));
        Game easyExpected = gameRepository.save(createGame(user1, GameLevel.EASY, 3, GameStatus.WON,  base.minusMinutes(5)));
                            gameRepository.save(createGame(user1, GameLevel.EASY, 3, GameStatus.WON,  base.minusMinutes(3)));

        // normal level
		                      gameRepository.save(createGame(user1, GameLevel.NORMAL, 1, GameStatus.GAVE_UP, base.minusMinutes(6)));
        Game normalExpected = gameRepository.save(createGame(user1, GameLevel.NORMAL, 2, GameStatus.WON,     base.minusMinutes(5)));
							  gameRepository.save(createGame(user1, GameLevel.NORMAL, 2, GameStatus.WON,     base.minusMinutes(4)));

        // hard level
		                    gameRepository.save(createGame(user1, GameLevel.HARD, 1, GameStatus.IN_PROGRESS, base.minusMinutes(7)));
        Game hardExpected = gameRepository.save(createGame(user1, GameLevel.HARD, 6, GameStatus.WON,         base.minusMinutes(3)));

        // other user (ignore)
        gameRepository.save(createGame(user2, GameLevel.EASY, 1, GameStatus.WON, base.minusMinutes(1)));

        List<Game> result = gameRepository.findBestGamesPerLevel(user1.getId());

        assertEquals(3, result.size());

        // EASY
        Game easy = result.stream()
                .filter(g -> g.getLevel() == GameLevel.EASY)
                .findFirst()
                .orElseThrow();

        assertEquals(easyExpected.getId(), easy.getId());
        assertEquals(3, easy.getAttemptsUsed());

        // NORMAL
        Game normal = result.stream()
                .filter(g -> g.getLevel() == GameLevel.NORMAL)
                .findFirst()
                .orElseThrow();

        assertEquals(normalExpected.getId(), normal.getId());
        assertEquals(2, normal.getAttemptsUsed());

        // HARD
        Game hard = result.stream()
                .filter(g -> g.getLevel() == GameLevel.HARD)
                .findFirst()
                .orElseThrow();

        assertEquals(hardExpected.getId(), hard.getId());
        assertEquals(6, hard.getAttemptsUsed());

        // check if all elements are WON
        assertTrue(result.stream()
                .allMatch(g -> g.getStatus() == GameStatus.WON));
    }

    @Test
    void shouldFindHistoryByUserIdOrderedDescAndIgnoreInProgress() {

        // 1. Create users
        User user1 = new User();
        user1.setName("User 1");
        user1.setEmail("user1@email.com");
        user1.setNickname("user1");
        user1.setAge(25);
        user1.setPassword("123");
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("User 2");
        user2.setEmail("user2@email.com");
        user2.setNickname("user2");
        user2.setAge(30);
        user2.setPassword("123");
        userRepository.save(user2);

        LocalDateTime base = LocalDateTime.now();

        // 2. Create history for USER 1
        Game g1 = gameRepository.save(createGame(user1, GameLevel.EASY, 5, GameStatus.WON, base.minusMinutes(5)));
        Game g2 = gameRepository.save(createGame(user1, GameLevel.NORMAL, 4, GameStatus.LOST, base.minusMinutes(3)));
        Game g3 = gameRepository.save(createGame(user1, GameLevel.HARD, 6, GameStatus.WON, base.minusMinutes(1)));

        // Should not appear
        gameRepository.save(createGame(user1, GameLevel.EASY, 2, GameStatus.IN_PROGRESS, base.minusMinutes(2)));

        // 3. Games USER 2 (ignore)
        gameRepository.save(createGame(user2, GameLevel.EASY, 1, GameStatus.WON, base.minusMinutes(1)));

        // 4. Execute
        List<Game> result = gameRepository.findHistoryByUserId(user1.getId());

        // 5. Validations
        // Should return 3 (ignoring IN_PROGRESS and other user)
        assertEquals(3, result.size());

        // DESC order (most recent first)
        assertEquals(g3.getId(), result.get(0).getId());
        assertEquals(g2.getId(), result.get(1).getId());
        assertEquals(g1.getId(), result.get(2).getId());

        // Ensure none are IN_PROGRESS
        assertTrue(result.stream().noneMatch(g -> g.getStatus() == GameStatus.IN_PROGRESS));

        // Ensure all are from user1
        assertTrue(result.stream().allMatch(g -> g.getUser().getId().equals(user1.getId())));
    }

    private Game createGame(User user, GameLevel level, int attempts, GameStatus status, LocalDateTime createdAt) {
        Game g = new Game();
        g.setUser(user);
        g.setLevel(level);
        g.setAttemptsUsed(attempts);
        g.setStatus(status);
        g.setCodeLength(4);
        g.setAllowDuplicates(true);
        g.setSecretCode("1234");
        g.setCreatedAt(createdAt);
        return g;
    }

    private Game createGame(User user) {
        return createGame(user, GameLevel.EASY, 4, GameStatus.IN_PROGRESS, LocalDateTime.now());
    }
}
