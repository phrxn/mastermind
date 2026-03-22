package com.quazzom.mastermind.unit.security;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.security.CustomUserDetails;
import com.quazzom.mastermind.security.CustomUserDetailsService;
import com.quazzom.mastermind.security.JwtAuthenticationFilter;
import com.quazzom.mastermind.service.JwtService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private CustomUserDetailsService customUserDetailsService;

	@InjectMocks
	private JwtAuthenticationFilter jwtAuthenticationFilter;

	@BeforeEach
	void setUp() {
		SecurityContextHolder.clearContext();
	}

	@AfterEach
	void tearDown() {
		SecurityContextHolder.clearContext();
	}

	// ===== doFilterInternal (sem header Authorization) =====
	@Test
	void doFilterShouldContinueChainWhenAuthorizationHeaderIsMissing() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertEquals(200, response.getStatus());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(jwtService, never()).isTokenValid(org.mockito.ArgumentMatchers.anyString());
	}

	// ===== doFilterInternal (token inválido) =====
	@Test
	void doFilterShouldReturnUnauthorizedWhenTokenIsInvalid() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer invalid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();

		when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertEquals(401, response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertEquals("{\"error\":\"Token inválido\",\"status\":401}", response.getContentAsString());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(customUserDetailsService, never()).loadUserByUuidPublic(org.mockito.ArgumentMatchers.any(UUID.class));
	}

	// ===== doFilterInternal (subject não é UUID válido) =====
	@Test
	void doFilterShouldReturnUnauthorizedWhenSubjectIsNotValidUuid() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn("abc");

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertEquals(401, response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertEquals("{\"error\":\"Token inválido\",\"status\":401}", response.getContentAsString());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
		verify(customUserDetailsService, never()).loadUserByUuidPublic(org.mockito.ArgumentMatchers.any(UUID.class));
	}

	// ===== doFilterInternal (token válido) =====
	@Test
	void doFilterShouldAuthenticateUserWhenTokenIsValid() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();

		User user = new User();
		UUID uuidPublic = UUID.randomUUID();
		user.setUuidPublic(uuidPublic);
		user.setEmail("user@email.com");
		user.setNickname("user");
		user.setPassword("password");
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn(uuidPublic.toString());
		when(customUserDetailsService.loadUserByUuidPublic(uuidPublic)).thenReturn(customUserDetails);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals(customUserDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		verify(customUserDetailsService).loadUserByUuidPublic(uuidPublic);
	}

	// ===== doFilterInternal (usuário não encontrado) =====
	@Test
	void doFilterShouldReturnUnauthorizedWhenUserIsNotFound() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();

		UUID uuidPublic = UUID.randomUUID();
		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn(uuidPublic.toString());
		when(customUserDetailsService.loadUserByUuidPublic(uuidPublic))
				.thenThrow(new UsernameNotFoundException("Usuário não encontrado"));

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertEquals(401, response.getStatus());
		assertEquals("application/json", response.getContentType());
		assertEquals("{\"error\":\"Token inválido\",\"status\":401}", response.getContentAsString());
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}
}