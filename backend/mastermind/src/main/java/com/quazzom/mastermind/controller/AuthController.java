package com.quazzom.mastermind.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.quazzom.mastermind.dto.LoginRequest;
import com.quazzom.mastermind.dto.LoginResponse;
import com.quazzom.mastermind.dto.MeResponse;
import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.security.CustomUserDetails;
import com.quazzom.mastermind.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final AuthService userService;

	public AuthController(AuthService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request,
			UriComponentsBuilder uriComponentsBuilder) {

		RegisterResponse createdUser = userService.register(request);

		return ResponseEntity.status(201).body(createdUser);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(userService.login(request));
	}

	@GetMapping("/me")
	public ResponseEntity<MeResponse> me(Authentication authentication) {
		UUID userId = extractUserId(authentication);
		return ResponseEntity.ok(userService.me(userId));
	}

	private UUID extractUserId(Authentication authentication) {

		Object principal = authentication.getPrincipal();
		if (principal instanceof CustomUserDetails details) {
			return details.getUuidPublic();
		}

		try {
			return UUID.fromString(authentication.getName());
		} catch (Exception ex) {
			throw new UnauthorizedException("Usuário não autenticado");
		}
	}

}