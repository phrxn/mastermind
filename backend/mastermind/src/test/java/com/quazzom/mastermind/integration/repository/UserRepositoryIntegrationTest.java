package com.quazzom.mastermind.integration.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Optional;

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
public class UserRepositoryIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GameRepository gameRepository;

	@Autowired
	private GuessRepository guessRepository;

	@BeforeEach
	void setUp() {
		guessRepository.deleteAll();
		gameRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void shouldSaveAndFindUserByEmail() {
		User user = buildValidUser("david@email.com", "david");
		userRepository.save(user);

		Optional<User> result = userRepository.findByEmail("david@email.com");

		assertTrue(result.isPresent());
		assertEquals("david@email.com", result.get().getEmail());
	}

	@Test
	void shouldSaveAndFindUserByNickname() {
		User user = buildValidUser("maria@email.com", "maria");
		userRepository.save(user);

		Optional<User> result = userRepository.findByNickname("maria");

		assertTrue(result.isPresent());
		assertEquals("maria", result.get().getNickname());
	}

	@Test
	void shouldReturnTrueWhenEmailAndNicknameExist() {
		User user = buildValidUser("joao@email.com", "joao");
		userRepository.save(user);

		assertTrue(userRepository.existsByEmail("joao@email.com"));
		assertTrue(userRepository.existsByNickname("joao"));
		assertFalse(userRepository.existsByEmail("naoexiste@email.com"));
		assertFalse(userRepository.existsByNickname("naoexiste"));
	}

	@Test
	void shouldPersistAllExplicitUserProperties() {
		User user = new User();
		user.setName("Ana");
		user.setEmail("ana@email.com");
		user.setNickname("ana");
		user.setAge(25);
		user.setPassword("senha-segura");
		user.setLoginAttempts(2);
		user.setBestScoreEasy(10);
		user.setBestScoreNormal(20);
		user.setBestScoreHard(30);
		user.setBestScoreMastermind(40);
		LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
		user.setCreatedAt(createdAt);

		User saved = userRepository.save(user);

		assertNotNull(saved.getId());
		assertEquals("Ana", saved.getName());
		assertEquals("ana@email.com", saved.getEmail());
		assertEquals("ana", saved.getNickname());
		assertEquals(25, saved.getAge());
		assertEquals("senha-segura", saved.getPassword());
		assertEquals(2, saved.getLoginAttempts());
		assertEquals(10, saved.getBestScoreEasy());
		assertEquals(20, saved.getBestScoreNormal());
		assertEquals(30, saved.getBestScoreHard());
		assertEquals(40, saved.getBestScoreMastermind());
		assertEquals(createdAt, saved.getCreatedAt());
	}

	@Test
	void shouldApplyDefaultValuesFromUserEntity() {
		User user = new User();
		user.setName("Carlos");
		user.setEmail("carlos@email.com");
		user.setNickname("carlos");
		user.setPassword("senha");

		User saved = userRepository.save(user);

		assertEquals(0, saved.getAge());
		assertEquals(0, saved.getLoginAttempts());
		assertEquals(0, saved.getBestScoreEasy());
		assertEquals(0, saved.getBestScoreNormal());
		assertEquals(0, saved.getBestScoreHard());
		assertEquals(0, saved.getBestScoreMastermind());
		assertNotNull(saved.getCreatedAt());
	}

	@Test
	void shouldDeleteUserById() {
		User user = buildValidUser("delete@email.com", "deleteUser");
		User saved = userRepository.save(user);

		userRepository.deleteById(saved.getId());

		assertFalse(userRepository.existsByEmail("delete@email.com"));
	}

	@Test
	void shouldUpdateUserInfoById() {
		User user = buildValidUser("old@email.com", "oldNick");
		User saved = userRepository.save(user);

		int rows = userRepository.updateUserInfoById(
			saved.getId(),
			"Novo Nome",
			"new@email.com",
			"newNick",
			33,
			"new-password"
		);

		Optional<User> updated = userRepository.findById(saved.getId());

		assertEquals(1, rows);
		assertTrue(updated.isPresent());
		assertEquals("Novo Nome", updated.get().getName());
		assertEquals("new@email.com", updated.get().getEmail());
		assertEquals("newNick", updated.get().getNickname());
		assertEquals(33, updated.get().getAge());
		assertEquals("new-password", updated.get().getPassword());
	}

	@Test
	void shouldDeleteUserCascadeGamesAndGuesses() {
		User user = buildValidUser("cascadeDelete@email.com", "cascadeJogador");
		User savedUser = userRepository.save(user);

		Game game = new Game();
		game.setUser(savedUser);
		game.setLevel(GameLevel.EASY);
		game.setCodeLength(4);
		game.setAllowDuplicates(false);
		game.setSecretCode("RGBY");
		game.setStatus(GameStatus.IN_PROGRESS);
		Game savedGame = gameRepository.save(game);

		Guess guess = new Guess();
		guess.setGame(savedGame);
		guess.setAttemptNumber(1);
		guess.setGuess("RRGG");
		guess.setCorrectPositions(1);
		guess.setCorrectColors(1);
		guessRepository.save(guess);

		userRepository.deleteById(savedUser.getId());

		assertFalse(userRepository.existsByEmail("cascadeDelete@email.com"));
		assertTrue(gameRepository.findByUserId(savedUser.getId()).isEmpty());
	}

	private User buildValidUser(String email, String nickname) {
		User user = new User();
		user.setName("Usuário Teste");
		user.setEmail(email);
		user.setNickname(nickname);
		user.setAge(20);
		user.setPassword("senha");
		return user;
	}


}
