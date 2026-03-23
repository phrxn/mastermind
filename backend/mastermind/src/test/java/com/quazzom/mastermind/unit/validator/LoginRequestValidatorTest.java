package com.quazzom.mastermind.unit.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.dto.LoginRequest;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.validator.LoginRequestValidator;

public class LoginRequestValidatorTest {

	private LoginRequestValidator loginRequestValidator;

	@BeforeEach
	void setUp() {
		loginRequestValidator = new LoginRequestValidator();
	}

	@Test
	void validateRequestBodyShouldPassWithValidData() {
		LoginRequest request = validRequest("maria@teste.com", "Abc123!");

		assertDoesNotThrow(() -> loginRequestValidator.validateRequestBody(request));
	}

	// ===== validateRequestBody() =====
	@Test
	void validateRequestBodyShouldThrowWhenUsernameIsNull() {
		LoginRequest request = validRequest(null, "Abc123!");

		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> loginRequestValidator.validateRequestBody(request));

		assertEquals("A propriedade 'username' deve existir.", exception.getMessage());
	}

	@Test
	void validateRequestBodyShouldThrowWhenPasswordIsNull() {
		LoginRequest request = validRequest("maria@teste.com", null);

		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> loginRequestValidator.validateRequestBody(request));

		assertEquals("A propriedade 'password' deve existir.", exception.getMessage());
	}

	// ===== isUsernameValid() =====
	@Test
	void isUsernameValidShouldThrowWhenUsernameIsEmpty() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> loginRequestValidator.isUsernameValid(""));

		assertEquals("O campo 'username' deve ser preenchido, ele não pode estar vazio", exception.getMessage());
	}

	@Test
	void isUsernameValidShouldThrowWhenUsernameHasOnlySpaces() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> loginRequestValidator.isUsernameValid("   "));

		assertEquals("O campo 'username' deve ser preenchido, ele não pode estar vazio", exception.getMessage());
	}

	@Test
	void isUsernameValidShouldPassWhenUsernameHasValue() {
		assertDoesNotThrow(() -> loginRequestValidator.isUsernameValid("maria@teste.com"));
	}

	// ===== isPasswordValid() =====
	@Test
	void isPasswordValidShouldThrowWhenPasswordIsEmpty() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> loginRequestValidator.isPasswordValid(""));

		assertEquals("O campo 'password' deve ser preenchido, ele não pode estar vazio", exception.getMessage());
	}

	@Test
	void isPasswordValidShouldThrowWhenPasswordHasOnlySpaces() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> loginRequestValidator.isPasswordValid("   "));

		assertEquals("O campo 'password' deve ser preenchido, ele não pode estar vazio", exception.getMessage());
	}

	@Test
	void isPasswordValidShouldPassWhenPasswordHasValue() {
		assertDoesNotThrow(() -> loginRequestValidator.isPasswordValid("Abc123!"));
	}

	private LoginRequest validRequest(String username, String password) {
		LoginRequest request = new LoginRequest(username, password);
		return request;
	}

}
