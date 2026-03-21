package com.quazzom.mastermind.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;

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
		Game game = buildValidGame(user);

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
		game.setScore(1000);
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
		assertEquals(1000, saved.getScore());
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

	private Game buildValidGame(User user) {
		Game game = new Game();
		game.setUser(user);
		game.setLevel(GameLevel.EASY);
		game.setCodeLength(4);
		game.setAllowDuplicates(false);
		game.setSecretCode("RGBY");
		game.setStatus(GameStatus.IN_PROGRESS);
		return game;
	}
}
