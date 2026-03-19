package com.quazzom.mastermind.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

		return ResponseEntity
				.status(500)
				.body(Map.of(
						"error", "Internal server error",
						"status", 500));
	}
}