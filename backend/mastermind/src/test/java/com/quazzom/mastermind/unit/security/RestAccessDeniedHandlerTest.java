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
import org.springframework.security.access.AccessDeniedException;

import com.quazzom.mastermind.security.RestAccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
class RestAccessDeniedHandlerTest {

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	@Mock
	private AccessDeniedException accessDeniedException;

	@InjectMocks
	private RestAccessDeniedHandler restAccessDeniedHandler;

	@BeforeEach
	void setUp() throws Exception {
		StringWriter stringWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(stringWriter);
		org.mockito.Mockito.when(response.getWriter()).thenReturn(printWriter);
	}

	// ===== handle =====
	@Test
	void handleShouldSetForbiddenStatusAndJsonResponse() throws Exception {
		restAccessDeniedHandler.handle(request, response, accessDeniedException);

		verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
		verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
	}

	@Test
	void handleShouldSetStatusCode403() throws Exception {
		restAccessDeniedHandler.handle(request, response, accessDeniedException);

		verify(response).setStatus(403);
	}

	@Test
	void handleShouldSetContentTypeToJson() throws Exception {
		restAccessDeniedHandler.handle(request, response, accessDeniedException);

		verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
	}
}
