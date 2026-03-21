package com.quazzom.mastermind.unit.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.businessrules.UserBusinessRule;
import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;
import com.quazzom.mastermind.validator.RegisterRequestValidator;

class RegisterRequestValidatorTest {

	private RegisterRequestValidator userValidator;

	@BeforeEach
	void setUp() {
		UserBusinessRule userBusinessRule = new UserBusinessRule("abc", "a@a.com", "abcdef", 1, "Abc@1224");
		userBusinessRule.setMessageDefaultForPropertiesJSON(new MessageDefaultForPropertiesJSON());
		userValidator = new RegisterRequestValidator(userBusinessRule);
	}

	@Test
	void validateRegisterShouldPassWithValidData() {
		RegisterRequest request = validRequest();

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	// ===== Name =====
	@Test
	void validateRegisterShouldFailWhenNameIsNull() {
		RegisterRequest request = validRequest();
		request.setName(null);

		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A propriedade 'name' deve existir.", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNameContainsNumber() {
		RegisterRequest request = validRequest();
		request.setName("Maria1 Silva");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNameContainsSpecialChar() {
		RegisterRequest request = validRequest();
		request.setName("Maria @Silva");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNameHasMoreThanSixtyChars() {
		RegisterRequest request = validRequest();
		request.setName("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHI");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres", exception.getMessage());
	}

	// ===== Email =====
	@Test
	void validateRegisterShouldFailWhenEmailIsNull() {
		RegisterRequest request = validRequest();
		request.setEmail(null);

		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A propriedade 'email' deve existir.", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenEmailIsEmpty() {
		RegisterRequest request = validRequest();
		request.setEmail("");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A propriedade 'email' não pode ser vazia.", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenEmailHasMoreThanFiftyChars() {
		RegisterRequest request = validRequest();
		request.setEmail("abcdefghijklmnopqrst@abcdefghijklmnopqrst.abcdefghi");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O email deve ter no máximo 50 caracteres", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenEmailHasNoAtSymbol() {
		RegisterRequest request = validRequest();
		request.setEmail("mariateste.com");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e TLD)", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenEmailHasNoDotAfterAt() {
		RegisterRequest request = validRequest();
		request.setEmail("maria@testecom");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e TLD)", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenEmailEndsWithDot() {
		RegisterRequest request = validRequest();
		request.setEmail("maria@testecom.");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e TLD)", exception.getMessage());
	}

	@Test
	void validateRegisterShouldPassWhenEmailHasMinimumFormat() {
		RegisterRequest request = validRequest();
		request.setEmail("a@b.cc");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	@Test
	void validateRegisterShouldFailWhenEmailTldHasOneCharacter() {
		RegisterRequest request = validRequest();
		request.setEmail("a@b.c");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e TLD)",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldPassWhenEmailHasSubdomainAndDomain() {
		RegisterRequest request = validRequest();
		request.setEmail("user@subdomain.domain.com");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	@Test
	void validateRegisterShouldPassWhenEmailHasFiftyChars() {
		RegisterRequest request = validRequest();
		request.setEmail("abcdefghijklmnopqrst@abcdefghijklmnopqrst.abcdefgh");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	// ===== Nickname =====
	@Test
	void validateRegisterShouldFailWhenNicknameIsNull() {
		RegisterRequest request = validRequest();
		request.setNickname(null);

		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A propriedade 'nickname' deve existir.", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameIsEmpty() {
		RegisterRequest request = validRequest();
		request.setNickname("");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameHasLessThanFourChars() {
		RegisterRequest request = validRequest();
		request.setNickname("abc");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameStartsWithNumber() {
		RegisterRequest request = validRequest();
		request.setNickname("1maria");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameStartsWithSpace() {
		RegisterRequest request = validRequest();
		request.setNickname(" maria");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameContainsUppercase() {
		RegisterRequest request = validRequest();
		request.setNickname("mariaSilva");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameContainsSpace() {
		RegisterRequest request = validRequest();
		request.setNickname("maria silva");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameContainsSpecialChar() {
		RegisterRequest request = validRequest();
		request.setNickname("maria_");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenNicknameHasMoreThanTwentyChars() {
		RegisterRequest request = validRequest();
		request.setNickname("abcdefghijklmnopqrstu");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldPassWhenNicknameHasNumbers() {
		RegisterRequest request = validRequest();
		request.setNickname("maria123");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	@Test
	void validateRegisterShouldPassWhenNicknameHasMinimumLength() {
		RegisterRequest request = validRequest();
		request.setNickname("a1b2");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	@Test
	void validateRegisterShouldPassWhenNicknameHasTwentyChars() {
		RegisterRequest request = validRequest();
		request.setNickname("a1b2c3d4e5f6g7h8i9j");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	// ===== Age =====
	@Test
	void validateRegisterShouldFailWhenAgeIsNull() {
		RegisterRequest request = validRequest();
		request.setAge(null);

		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A propriedade 'age' deve existir.", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenAgeIsOutOfRange() {
		RegisterRequest request = validRequest();
		request.setAge(0);

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenAgeIsGreaterThanOneHundredTwenty() {
		RegisterRequest request = validRequest();
		request.setAge(121);

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
	}

	@Test
	void validateRegisterShouldPassWhenAgeIsLowerBoundary() {
		RegisterRequest request = validRequest();
		request.setAge(1);

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	@Test
	void validateRegisterShouldPassWhenAgeIsUpperBoundary() {
		RegisterRequest request = validRequest();
		request.setAge(120);

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	// ===== Password =====
	@Test
	void validateRegisterShouldFailWhenPasswordIsNull() {
		RegisterRequest request = validRequest();
		request.setPassword(null);

		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A propriedade 'password' deve existir.", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordIsEmpty() {
		RegisterRequest request = validRequest();
		request.setPassword("");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A senha deve ter entre 6 e 20 caracteres", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordLengthIsTooShort() {
		RegisterRequest request = validRequest();
		request.setPassword("Ab1!");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A senha deve ter entre 6 e 20 caracteres", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordLengthIsTooLong() {
		RegisterRequest request = validRequest();
		request.setPassword("AbcdefghijK1234!mnopq");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals("A senha deve ter entre 6 e 20 caracteres", exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordLacksUppercase() {
		RegisterRequest request = validRequest();
		request.setPassword("abc123!");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordLacksLowercase() {
		RegisterRequest request = validRequest();
		request.setPassword("ABC123!");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordLacksNumber() {
		RegisterRequest request = validRequest();
		request.setPassword("Abcdef!");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordLacksSpecialChar() {
		RegisterRequest request = validRequest();
		request.setPassword("Abc1234");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldFailWhenPasswordHasNotAllowedSpecialChar() {
		RegisterRequest request = validRequest();
		request.setPassword("Abc123*");

		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userValidator.validateRequestBody(request));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void validateRegisterShouldPassWhenPasswordHasMinimumLength() {
		RegisterRequest request = validRequest();
		request.setPassword("Ab1!cd");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	@Test
	void validateRegisterShouldPassWhenPasswordHasMaximumLength() {
		RegisterRequest request = validRequest();
		request.setPassword("AbcdefghijK1234!mnop");

		assertDoesNotThrow(() -> userValidator.validateRequestBody(request));
	}

	private RegisterRequest validRequest() {
		RegisterRequest request = new RegisterRequest();
		request.setName("Maria Silva");
		request.setEmail("maria@teste.com");
		request.setNickname("maria");
		request.setAge(25);
		request.setPassword("Abc123!");
		return request;
	}
}
