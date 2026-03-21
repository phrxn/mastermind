package com.quazzom.mastermind.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import com.quazzom.mastermind.controller.GameController;
import com.quazzom.mastermind.dto.GameCreateRequest;
import com.quazzom.mastermind.dto.GameCreateResponse;
import com.quazzom.mastermind.dto.GameGiveUpResponse;
import com.quazzom.mastermind.dto.GameGuessRequest;
import com.quazzom.mastermind.dto.GameGuessResponse;
import com.quazzom.mastermind.dto.GameStatusResponse;
import com.quazzom.mastermind.service.GameService;

@ExtendWith(MockitoExtension.class)
class GameControllerTest {

	@Mock
	private GameService gameService;

	@Mock
	private Authentication authentication;

	@InjectMocks
	private GameController gameController;

	@Test
	void createGameShouldReturnCreated() {
		GameCreateRequest request = new GameCreateRequest();
		request.setLevel(1);
		GameCreateResponse expected = new GameCreateResponse("GAME_IN_PROGRESS", 1);

		when(authentication.getName()).thenReturn("10");
		when(gameService.createGame(10L, 1)).thenReturn(expected);

		ResponseEntity<GameCreateResponse> response = gameController.createGame(request, authentication);

		assertEquals(HttpStatus.CREATED, response.getStatusCode());
		assertSame(expected, response.getBody());
	}

	@Test
	void makeGuessShouldReturnOk() {
		GameGuessRequest request = new GameGuessRequest();
		request.setGuess(List.of(1, 2, 3, 4));
		GameGuessResponse expected = new GameGuessResponse("GAME_IN_PROGRESS", 1, List.of(1, 2), null);

		when(authentication.getName()).thenReturn("10");
		when(gameService.makeGuess(10L, List.of(1, 2, 3, 4))).thenReturn(expected);

		ResponseEntity<GameGuessResponse> response = gameController.makeGuess(request, authentication);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(expected, response.getBody());
	}

	@Test
	void giveUpShouldReturnOk() {
		GameGiveUpResponse expected = new GameGiveUpResponse("GAME_GIVE_UP", 1, List.of(1, 2, 3, 4));

		when(authentication.getName()).thenReturn("10");
		when(gameService.giveUp(10L)).thenReturn(expected);

		ResponseEntity<GameGiveUpResponse> response = gameController.giveUp(authentication);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(expected, response.getBody());
	}

	@Test
	void statusShouldReturnNoContentWhenNoGameExists() {
		when(authentication.getName()).thenReturn("10");
		when(gameService.status(10L)).thenReturn(Optional.empty());

		ResponseEntity<?> response = gameController.status(authentication);

		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
	}

	@Test
	void statusShouldReturnOkWhenGameExists() {
		GameStatusResponse expected = new GameStatusResponse("GAME_IN_PROGRESS", 2, List.of());

		when(authentication.getName()).thenReturn("10");
		when(gameService.status(10L)).thenReturn(Optional.of(expected));

		ResponseEntity<?> response = gameController.status(authentication);

		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertSame(expected, response.getBody());
		verify(gameService).status(10L);
	}
}
