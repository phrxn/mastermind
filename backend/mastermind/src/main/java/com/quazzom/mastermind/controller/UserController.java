package com.quazzom.mastermind.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quazzom.mastermind.dto.GameHistoryResponse;
import com.quazzom.mastermind.dto.UserPasswordRequest;
import com.quazzom.mastermind.dto.UserProfileRequest;
import com.quazzom.mastermind.dto.UserProfileResponse;
import com.quazzom.mastermind.exception.UnauthorizedException;
import com.quazzom.mastermind.security.CustomUserDetails;
import com.quazzom.mastermind.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Authentication authentication) {
        UUID uuidPublic = extractUserId(authentication);
        return ResponseEntity.ok(userService.getProfile(uuidPublic));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @RequestBody UserProfileRequest request,
            Authentication authentication) {
        UUID uuidPublic = extractUserId(authentication);
        return ResponseEntity.ok(userService.updateProfile(uuidPublic, request));
    }

    @DeleteMapping("/profile")
    public ResponseEntity<Void> deleteProfile(Authentication authentication) {
        UUID uuidPublic = extractUserId(authentication);
        userService.deleteProfile(uuidPublic);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password")
    public ResponseEntity<Void> updatePassword(
            @RequestBody UserPasswordRequest request,
            Authentication authentication) {
        UUID uuidPublic = extractUserId(authentication);
        userService.updatePassword(uuidPublic, request);
        return ResponseEntity.noContent().build();
    }

	@GetMapping("/history")
	public ResponseEntity<GameHistoryResponse> getHistory(Authentication authentication) {
		UUID uuidPublic = extractUserId(authentication);
		return ResponseEntity.ok(userService.getHistory(uuidPublic));
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