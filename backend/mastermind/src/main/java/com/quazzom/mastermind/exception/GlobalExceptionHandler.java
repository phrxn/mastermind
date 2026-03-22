package com.quazzom.mastermind.exception;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<?> handleApiException(ApiException ex) {

		return ResponseEntity
				.status(ex.getStatus())
				.body(Map.of(
						"error", ex.getMessage(),
						"status", ex.getStatus()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

		String errorMessage = "Corpo da requisição vazio ou em um formato inválido. Ele deve ser um JSON válido contendo os campos esperados e com os valores e tipos corretos.";

		return ResponseEntity
				.status(400)
				.body(Map.of(
						"error", errorMessage,
						"status", 400));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGenericException(Exception ex) {

		if (ex instanceof ErrorResponse errorResponse) {
			int status = errorResponse.getStatusCode().value();
			String errorMessage = errorResponse.getBody().getDetail();

			if (errorMessage == null || errorMessage.isBlank()) {
				errorMessage = "Request failed";
			}

			return ResponseEntity
					.status(status)
					.body(Map.of(
							"error", errorMessage,
							"status", status));
		}

		System.err.println("Unexpected error: " + ex.getMessage());

		return ResponseEntity
				.status(500)
				.body(Map.of(
						"error", "Internal server error",
						"status", 500));
	}
}