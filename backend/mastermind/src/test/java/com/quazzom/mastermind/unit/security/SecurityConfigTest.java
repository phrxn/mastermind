package com.quazzom.mastermind.unit.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.Answers.RETURNS_SELF;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.quazzom.mastermind.security.JwtAuthenticationFilter;
import com.quazzom.mastermind.security.RestAccessDeniedHandler;
import com.quazzom.mastermind.security.RestAuthenticationEntryPoint;
import com.quazzom.mastermind.security.SecurityConfig;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {

	@Mock
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@Mock
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@Mock
	private RestAccessDeniedHandler restAccessDeniedHandler;

	@InjectMocks
	private SecurityConfig securityConfig;

	// ===== passwordEncoder =====
	@Test
	void passwordEncoderShouldReturnBCryptPasswordEncoder() {
		PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

		assertNotNull(passwordEncoder);
		assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
	}

	// ===== securityFilterChain =====
	@Test
	void securityFilterChainShouldAddJwtFilterBeforeUsernamePasswordFilter() throws Exception {
		HttpSecurity httpSecurity = mock(HttpSecurity.class, RETURNS_SELF);
		DefaultSecurityFilterChain expectedFilterChain = mock(DefaultSecurityFilterChain.class);
		when(httpSecurity.build()).thenReturn(expectedFilterChain);

		SecurityFilterChain filterChain = securityConfig.securityFilterChain(httpSecurity);

		assertNotNull(filterChain);
		verify(httpSecurity).addFilterBefore(eq(jwtAuthenticationFilter), eq(UsernamePasswordAuthenticationFilter.class));
		verify(httpSecurity).csrf(any());
		verify(httpSecurity).cors(any());
		verify(httpSecurity).sessionManagement(any());
		verify(httpSecurity).authorizeHttpRequests(any());
		verify(httpSecurity).exceptionHandling(any());
		verify(httpSecurity).build();
	}
}