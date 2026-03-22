package com.quazzom.mastermind.unit.security;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.repository.UserRepository;
import com.quazzom.mastermind.security.CustomUserDetailsService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CustomUserDetailsServiceTest {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private CustomUserDetailsService customUserDetailsService;

	private User testUser;

	@BeforeEach
	void setUp() {
		testUser = new User();
		testUser.setId(1L);
		testUser.setUuidPublic(UUID.randomUUID());
		testUser.setEmail("test@email.com");
		testUser.setNickname("testuser");
		testUser.setPassword("password");
	}

	// ===== loadUserByUsername (by email) =====
	@Test
	void loadUserByUsernameShouldFindByEmailAndReturnUserDetails() {
		when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.of(testUser));
		when(userRepository.findByNickname("test@email.com")).thenReturn(Optional.empty());

		UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@email.com");

		assertEquals("test@email.com", userDetails.getUsername());
		assertEquals("password", userDetails.getPassword());
	}

	// ===== loadUserByUsername (by nickname) =====
	@Test
	void loadUserByUsernameShouldFallbackToNicknameWhenEmailNotFound() {
		when(userRepository.findByEmail("testuser")).thenReturn(Optional.empty());
		when(userRepository.findByNickname("testuser")).thenReturn(Optional.of(testUser));

		UserDetails userDetails = customUserDetailsService.loadUserByUsername("testuser");

		assertEquals("test@email.com", userDetails.getUsername());
		assertEquals("password", userDetails.getPassword());
	}

	// ===== loadUserByUsername (not found) =====
	@Test
	void loadUserByUsernameShouldThrowWhenUserNotFoundByEmailOrNickname() {
		when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());
		when(userRepository.findByNickname("unknown")).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
				() -> customUserDetailsService.loadUserByUsername("unknown"));

		assertEquals("Usuário não encontrado", exception.getMessage());
	}

	// ===== loadUserByUuidPublic =====
	@Test
	void loadUserByUuidPublicShouldFindByUuidAndReturnUserDetails() {
		when(userRepository.findByUuidPublic(testUser.getUuidPublic())).thenReturn(Optional.of(testUser));

		UserDetails userDetails = customUserDetailsService.loadUserByUuidPublic(testUser.getUuidPublic());

		assertEquals("test@email.com", userDetails.getUsername());
		assertEquals("password", userDetails.getPassword());
	}

	// ===== loadUserByUuidPublic (not found) =====
	@Test
	void loadUserByUuidPublicShouldThrowWhenUserNotFound() {
		UUID unknownUuid = UUID.randomUUID();
		when(userRepository.findByUuidPublic(unknownUuid)).thenReturn(Optional.empty());

		UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
				() -> customUserDetailsService.loadUserByUuidPublic(unknownUuid));

		assertEquals("Usuário não encontrado", exception.getMessage());
	}
}
