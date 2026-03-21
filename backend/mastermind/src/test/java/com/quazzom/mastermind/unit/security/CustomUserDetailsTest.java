package com.quazzom.mastermind.unit.security;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.security.CustomUserDetails;

class CustomUserDetailsTest {

	private CustomUserDetails customUserDetails;
	private User user;

	@BeforeEach
	void setUp() {
		user = new User();
		user.setId(1L);
		user.setEmail("test@email.com");
		user.setNickname("testuser");
		user.setPassword("encrypted_password");
		user.setName("Test User");

		customUserDetails = new CustomUserDetails(user);
	}

	// ===== getUsername =====
	@Test
	void getUsernameShouldReturnUserEmail() {
		assertEquals("test@email.com", customUserDetails.getUsername());
	}

	// ===== getPassword =====
	@Test
	void getPasswordShouldReturnUserPassword() {
		assertEquals("encrypted_password", customUserDetails.getPassword());
	}

	// ===== getId =====
	@Test
	void getIdShouldReturnUserId() {
		assertEquals(1L, customUserDetails.getId());
	}

	// ===== getEmail =====
	@Test
	void getEmailShouldReturnUserEmail() {
		assertEquals("test@email.com", customUserDetails.getEmail());
	}

	// ===== getNickname =====
	@Test
	void getNicknameShouldReturnUserNickname() {
		assertEquals("testuser", customUserDetails.getNickname());
	}

	// ===== getAuthorities =====
	@Test
	void getAuthoritiesShouldReturnRoleUser() {
		Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();

		assertEquals(1, authorities.size());
		assertTrue(authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));
	}

	// ===== isAccountNonExpired =====
	@Test
	void isAccountNonExpiredShouldReturnTrue() {
		assertTrue(customUserDetails.isAccountNonExpired());
	}

	// ===== isAccountNonLocked =====
	@Test
	void isAccountNonLockedShouldReturnTrue() {
		assertTrue(customUserDetails.isAccountNonLocked());
	}

	// ===== isCredentialsNonExpired =====
	@Test
	void isCredentialsNonExpiredShouldReturnTrue() {
		assertTrue(customUserDetails.isCredentialsNonExpired());
	}

	// ===== isEnabled =====
	@Test
	void isEnabledShouldReturnTrue() {
		assertTrue(customUserDetails.isEnabled());
	}
}
