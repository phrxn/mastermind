package com.quazzom.mastermind.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import com.quazzom.mastermind.controller.AuthController;
import com.quazzom.mastermind.dto.LoginRequest;
import com.quazzom.mastermind.dto.LoginResponse;
import com.quazzom.mastermind.dto.MeResponse;
import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.GlobalExceptionHandler;
import com.quazzom.mastermind.service.AuthService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService userService;

    @InjectMocks
    private AuthController authController;

    @Mock
    private Authentication authentication;

    @Test
    void registerShouldReturnServiceResponse() {
        RegisterRequest request = new RegisterRequest();
        User user = new User();
        user.setId(10L);
        RegisterResponse expected = new RegisterResponse(user);

        when(userService.register(request)).thenReturn(expected);

        ResponseEntity<RegisterResponse> actual = authController.register(request,
                UriComponentsBuilder.fromUriString("http://localhost"));

        assertEquals(HttpStatus.CREATED, actual.getStatusCode());
        assertSame(expected, actual.getBody());
        verify(userService).register(request);
    }

    @Test
    void registerShouldReturnMethodNotAllowedForGetWhenGlobalHandlerIsApplied() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        mockMvc.perform(get("/auth/register"))
            .andExpect(status().isMethodNotAllowed())
            .andExpect(jsonPath("$.status").value(405))
            .andExpect(jsonPath("$.error").value("Method 'GET' is not supported."));

        verify(userService, never()).register(org.mockito.ArgumentMatchers.any(RegisterRequest.class));
    }

    @Test
    void loginShouldReturnServiceResponse() {
        LoginRequest request = new LoginRequest();
        request.setUsername("maria@teste.com");
        request.setPassword("Abc123!");
        LoginResponse expected = new LoginResponse("jwt-token");

        when(userService.login(request)).thenReturn(expected);

        ResponseEntity<LoginResponse> actual = authController.login(request);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertSame(expected, actual.getBody());
        verify(userService).login(request);
    }

    @Test
    void meShouldReturnAuthenticatedUserData() {
        User user = new User();
        user.setId(10L);
        UUID uuidPublic = UUID.randomUUID();
        user.setUuidPublic(uuidPublic);
        user.setName("Maria Silva");
        user.setEmail("maria@teste.com");
        user.setNickname("maria");
        user.setAge(25);
        MeResponse expected = new MeResponse(user);

        when(authentication.getName()).thenReturn(uuidPublic.toString());
        when(userService.me(uuidPublic)).thenReturn(expected);

        ResponseEntity<MeResponse> actual = authController.me(authentication);

        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertTrue(actual.getBody().isAuthenticated());
        assertSame(expected, actual.getBody());
		verify(userService).me(uuidPublic);
    }
}