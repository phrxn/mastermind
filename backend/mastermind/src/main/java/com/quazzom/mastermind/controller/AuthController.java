package com.quazzom.mastermind.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.Authentication;

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
		URI location = uriComponentsBuilder.path("/users/{id}").buildAndExpand(createdUser.getId()).toUri();

		return ResponseEntity.created(location).body(createdUser);
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(userService.login(request));
	}

	@GetMapping("/me")
	public ResponseEntity<MeResponse> me(Authentication authentication) {
		Long userId = extractUserId(authentication);
		return ResponseEntity.ok(userService.me(userId));
	}

	private Long extractUserId(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof CustomUserDetails details) {
			return details.getId();
		}

		try {
			return Long.parseLong(authentication.getName());
		} catch (Exception ex) {
			throw new UnauthorizedException("Usuário não autenticado");
		}
	}

}