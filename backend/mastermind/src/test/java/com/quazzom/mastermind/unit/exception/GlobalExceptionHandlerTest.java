package com.quazzom.mastermind.unit.exception;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.server.ResponseStatusException;

import com.quazzom.mastermind.exception.ApiException;
import com.quazzom.mastermind.exception.GlobalExceptionHandler;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleApiExceptionShouldReturnCustomStatusAndBody() {
        ApiException exception = new TestApiException("Erro de negócio", 422);

        ResponseEntity<?> response = handler.handleApiException(exception);

        assertEquals(422, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Erro de negócio", body.get("error"));
        assertEquals(422, body.get("status"));
    }

    @Test
    void handleGenericExceptionShouldReturnInternalServerError() {
        ResponseEntity<?> response = handler.handleGenericException(new RuntimeException("falha"));

        assertEquals(500, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Internal server error", body.get("error"));
        assertEquals(500, body.get("status"));
    }

    @Test
    void handleGenericExceptionShouldPreserveKnownHttpStatusFromSpringExceptions() {
        ResponseEntity<?> response = handler.handleGenericException(
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No static resource rota-inexistente")
        );

        assertEquals(404, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("No static resource rota-inexistente", body.get("error"));
        assertEquals(404, body.get("status"));
    }

    @Test
    void handleHttpMessageNotReadableShouldReturnBadRequest() {

        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Corpo vazio", (Throwable)null, null);

        ResponseEntity<?> response = handler.handleHttpMessageNotReadable(exception);

        assertEquals(400, response.getStatusCode().value());
        Map<?, ?> body = (Map<?, ?>) response.getBody();
        assertEquals("Corpo da requisição vazio ou em um formato inválido. Ele deve ser um JSON válido contendo os campos esperados e com os valores e tipos corretos.", body.get("error"));
        assertEquals(400, body.get("status"));
    }

    private static class TestApiException extends ApiException {
        TestApiException(String message, int status) {
            super(message, status);
        }
    }
}