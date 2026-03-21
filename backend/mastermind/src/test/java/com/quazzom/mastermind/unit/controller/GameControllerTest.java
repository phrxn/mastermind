package com.quazzom.mastermind.unit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.quazzom.mastermind.controller.GameController;
import com.quazzom.mastermind.dto.GameCreateRequest;
import com.quazzom.mastermind.dto.GameEndResponse;
import com.quazzom.mastermind.dto.GameGuessRequest;
import com.quazzom.mastermind.dto.GameStatusResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.security.CustomUserDetails;
import com.quazzom.mastermind.service.GameService;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

	@Mock
	private GameService gameService;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private GameController gameController;

	// ===== createGame =====
	@Test
	void createGameShouldReturnCreatedWhenLevelIsValid() {
		GameCreateRequest request = new GameCreateRequest();
		request.setLevel(1);
		GameStatusResponse expected = new GameStatusResponse("GAME_IN_PROGRESS", 1, 4, 10, false, new ArrayList<>());

		when(authentication.getName()).thenReturn("10");
		when(gameService.createGame(10L, 1)).thenReturn(expected);

		ResponseEntity<GameStatusResponse> response = gameController.createGame(request, authentication);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(expected, response.getBody());
	}

	@Test
	void createGameShouldExtractUserIdFromCustomUserDetails() {
		User user = new User();
		user.setId(25L);
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		GameCreateRequest request = new GameCreateRequest();
		request.setLevel(2);
		GameStatusResponse expected = new GameStatusResponse("GAME_IN_PROGRESS", 2, 4, 10, true, new ArrayList<>());

		when(authentication.getPrincipal()).thenReturn(customUserDetails);
		when(gameService.createGame(25L, 2)).thenReturn(expected);

		ResponseEntity<GameStatusResponse> response = gameController.createGame(request, authentication);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertEquals(expected, response.getBody());
	}

	// ===== makeGuess =====
	@Test
	void makeGuessShouldReturnOkWhenGuessIsValid() {
		GameGuessRequest request = new GameGuessRequest();
		request.setGuess(List.of(1, 2, 3, 4));
		GameEndResponse expected = new GameEndResponse("GAME_IN_PROGRESS", 1, List.of(1, 2, 3, 4));

		when(authentication.getName()).thenReturn("15");
		when(gameService.makeGuess(15L, List.of(1, 2, 3, 4))).thenReturn(expected);

		ResponseEntity<?> response = gameController.makeGuess(request, authentication);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expected, response.getBody());
	}

	// ===== giveUp =====
	@Test
	void giveUpShouldReturnOkWhenGameExistsAndGiveUp() {
		GameEndResponse expected = new GameEndResponse("GAME_GIVE_UP", 1, List.of(1, 2, 3, 4));

		when(authentication.getName()).thenReturn("20");
		when(gameService.giveUp(20L)).thenReturn(expected);

		ResponseEntity<GameEndResponse> response = gameController.giveUp(authentication);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expected, response.getBody());
	}

	// ===== status =====
	@Test
	void statusShouldReturnNoContentWhenNoGameInProgress() {
		when(authentication.getName()).thenReturn("30");
		when(gameService.status(30L)).thenReturn(Optional.empty());

		ResponseEntity<?> response = gameController.status(authentication);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void statusShouldReturnOkWhenGameInProgress() {
		GameStatusResponse expected = new GameStatusResponse("GAME_IN_PROGRESS", 2, 4, 10, true, new ArrayList<>());

		when(authentication.getName()).thenReturn("35");
		when(gameService.status(35L)).thenReturn(Optional.of(expected));

		ResponseEntity<?> response = gameController.status(authentication);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals(expected, response.getBody());
	}

	// ===== authentication error cases =====
	@Test
	void createGameShouldThrowUnauthorizedWhenAuthenticationIsInvalid() {
		GameCreateRequest request = new GameCreateRequest();
		request.setLevel(1);

		when(authentication.getPrincipal()).thenReturn("invalidPrincipal");
		when(authentication.getName()).thenReturn("invalidName");

		UnauthorizedException exception = assertThrows(UnauthorizedException.class,
				() -> gameController.createGame(request, authentication));

		assertEquals("Usuário não autenticado", exception.getMessage());
	}
}
