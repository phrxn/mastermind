package com.quazzom.mastermind.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {

	private final UserService userService;

	public AuthController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/register")
	public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request,
			UriComponentsBuilder uriComponentsBuilder) {
		RegisterResponse createdUser = userService.register(request);
		URI location = uriComponentsBuilder.path("/users/{id}").buildAndExpand(createdUser.getId()).toUri();

		return ResponseEntity.created(location).body(createdUser);
	}
}