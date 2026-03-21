package com.quazzom.mastermind.unit.security;

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
		verify(customUserDetailsService, never()).loadUserById(org.mockito.ArgumentMatchers.anyLong());
	}

	// ===== doFilterInternal (subject não numérico) =====
	@Test
	void doFilterShouldReturnUnauthorizedWhenSubjectIsNotNumeric() throws Exception {
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
		verify(customUserDetailsService, never()).loadUserById(org.mockito.ArgumentMatchers.anyLong());
	}

	// ===== doFilterInternal (token válido) =====
	@Test
	void doFilterShouldAuthenticateUserWhenTokenIsValid() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addHeader("Authorization", "Bearer valid-token");
		MockHttpServletResponse response = new MockHttpServletResponse();
		MockFilterChain filterChain = new MockFilterChain();

		User user = new User();
		user.setId(1L);
		user.setEmail("user@email.com");
		user.setNickname("user");
		user.setPassword("password");
		CustomUserDetails customUserDetails = new CustomUserDetails(user);

		when(jwtService.isTokenValid("valid-token")).thenReturn(true);
		when(jwtService.extractSubject("valid-token")).thenReturn("1");
		when(customUserDetailsService.loadUserById(1L)).thenReturn(customUserDetails);

		jwtAuthenticationFilter.doFilter(request, response, filterChain);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
		assertEquals(customUserDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		verify(customUserDetailsService).loadUserById(1L);
	}
}