package com.quazzom.mastermind.unit.security;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;

import com.quazzom.mastermind.security.RestAuthenticationEntryPoint;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class RestAuthenticationEntryPointTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private AuthenticationException authException;

	@InjectMocks
	private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

	@BeforeEach
	void setUp() throws Exception {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		org.mockito.Mockito.when(response.getWriter()).thenReturn(printWriter);
	}

	// ===== commence =====
	@Test
	void commenceShouldSetUnauthorizedStatusAndJsonResponse() throws Exception {
		restAuthenticationEntryPoint.commence(request, response, authException);

		verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	void commenceShouldSetStatusCode401() throws Exception {
		restAuthenticationEntryPoint.commence(request, response, authException);

		verify(response).setStatus(401);
	}

	@Test
	void commenceShouldSetContentTypeToJson() throws Exception {
		restAuthenticationEntryPoint.commence(request, response, authException);

		verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
	}
}
