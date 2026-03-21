package com.quazzom.mastermind.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quazzom.mastermind.businessrules.GameBusinessRole;
import com.quazzom.mastermind.dto.GameCreateRequest;
import com.quazzom.mastermind.dto.GameEndResponse;
import com.quazzom.mastermind.dto.GameGuessRequest;
import com.quazzom.mastermind.dto.GameResponse;
import com.quazzom.mastermind.dto.GameStatusResponse;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.security.CustomUserDetails;
import com.quazzom.mastermind.service.GameService;

@RestController
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService, GameBusinessRole gameBusinessRole) {
        this.gameService = gameService;
    }

    @PostMapping("/create")
    public ResponseEntity<GameStatusResponse> createGame(
            @RequestBody GameCreateRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        GameStatusResponse response = gameService.createGame(userId, request.getLevel());
        return ResponseEntity.status(201).body(response);
    }

    @PostMapping("/guess")
    public ResponseEntity<GameResponse> makeGuess(
            @RequestBody GameGuessRequest request,
            Authentication authentication) {

        Long userId = extractUserId(authentication);
        GameResponse response = gameService.makeGuess(userId, request.getGuess());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/give-up")
    public ResponseEntity<GameEndResponse> giveUp(Authentication authentication) {
        Long userId = extractUserId(authentication);
        GameEndResponse response = gameService.giveUp(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(Authentication authentication) {
        Long userId = extractUserId(authentication);
        Optional<GameStatusResponse> response = gameService.status(userId);
        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(response.get());
    }

    private Long extractUserId(Authentication authentication) {

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails details) {
            return details.getId();
        }

        try {
            return Long.valueOf(authentication.getName());
        } catch (Exception ex) {
            throw new UnauthorizedException("Usuário não autenticado");
        }
    }
}
