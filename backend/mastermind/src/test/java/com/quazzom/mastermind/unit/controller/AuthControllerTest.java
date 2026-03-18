package com.quazzom.mastermind.unit.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import com.quazzom.mastermind.controller.AuthController;
import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.entity.User;
import com.quazzom.mastermind.exception.GlobalExceptionHandler;
import com.quazzom.mastermind.service.UserService;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

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
        assertEquals("http://localhost/users/10", actual.getHeaders().getLocation().toString());
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
            .andExpect(jsonPath("$.error").value("Method not allowed"));

        verify(userService, never()).register(org.mockito.ArgumentMatchers.any(RegisterRequest.class));
    }
}