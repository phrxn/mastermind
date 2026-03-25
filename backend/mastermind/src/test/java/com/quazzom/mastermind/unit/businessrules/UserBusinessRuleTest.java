package com.quazzom.mastermind.unit.businessrules;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.businessrules.UserBusinessRule;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.utils.MessageDefaultForPropertiesJSON;

class UserBusinessRuleTest {

	private UserBusinessRule userBusinessRule;
	private MessageDefaultForPropertiesJSON messageDefaultForPropertiesJSON;

	@BeforeEach
	void setUp() {
		messageDefaultForPropertiesJSON = new MessageDefaultForPropertiesJSON();
		userBusinessRule = new UserBusinessRule(null, "Maria Silva", "maria@teste.com", "maria",
				25, "Abc123!", 0, LocalDateTime.now(),
				messageDefaultForPropertiesJSON);
	}


	// ===== Name =====
	@Test
	void setNameShouldPassWithValidName() {
		assertDoesNotThrow(() -> userBusinessRule.setName("Maria Silva"));
		assertEquals("Maria Silva", userBusinessRule.getName());
	}


	@Test
	void setNameShouldFailWhenNameIsNull() {
		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userBusinessRule.setName(null));
		assertEquals("A propriedade 'name' deve existir.", exception.getMessage());
	}

	@Test
	void setNameShouldFailWhenNameStartsWithWhitespace() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setName(" Maria Silva"));
		assertEquals("O nome não pode começar ou terminar com espaços", exception.getMessage());
	}

	@Test
	void setNameShouldFailWhenNameEndsWithWhitespace() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setName("Maria Silva "));
		assertEquals("O nome não pode começar ou terminar com espaços", exception.getMessage());
	}

	@Test
	void setNameShouldFailWhenNameContainsNumber() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setName("Maria1 Silva"));
		assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres",
				exception.getMessage());
	}

	@Test
	void setNameShouldFailWhenNameContainsSpecialChar() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setName("Maria @Silva"));
		assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres",
				exception.getMessage());
	}

	@Test
	void setNameShouldFailWhenNameHasMoreThanSixtyChars() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setName("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHI"));
		assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres",
				exception.getMessage());
	}

	@Test
	void setNameShouldPassWhenNameHasSixtyChars() {
		String nameWithSixtyChars = "A".repeat(60);
		assertDoesNotThrow(() -> userBusinessRule.setName(nameWithSixtyChars));
		assertEquals(nameWithSixtyChars, userBusinessRule.getName());
	}

	@Test
	void setNameShouldPassWhenNameHasSpaces() {
		assertDoesNotThrow(() -> userBusinessRule.setName("Maria de Silva Santos"));
		assertEquals("Maria de Silva Santos", userBusinessRule.getName());
	}





	// ===== Email =====
	@Test
	void setEmailShouldPassWithValidEmail() {
		assertDoesNotThrow(() -> userBusinessRule.setEmail("maria@teste.com"));
		assertEquals("maria@teste.com", userBusinessRule.getEmail());
	}


	@Test
	void setEmailShouldPassWithAComplexButValidEmail(){
		assertDoesNotThrow(() -> userBusinessRule.setEmail("user.name+tag-123@example-domain.co"));
		assertEquals("user.name+tag-123@example-domain.co", userBusinessRule.getEmail());
	}

	@Test
	void setEmailShouldFailWhenEmailIsNull() {
		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userBusinessRule.setEmail(null));
		assertEquals("A propriedade 'email' deve existir.", exception.getMessage());
	}

	@Test
	void setEmailShouldFailWhenEmailIsEmpty() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setEmail(""));
		assertEquals("A propriedade 'email' não pode ser vazia.", exception.getMessage());
	}

	@Test
	void setEmailShouldFailWhenEmailIsBlank() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setEmail("   "));
		assertEquals("A propriedade 'email' não pode ser vazia.", exception.getMessage());
	}

	@Test
	void setEmailShouldFailWhenEmailHasMoreThanFiftyChars() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setEmail("abcdefghijklmnopqrst@abcdefghijklmnopqrst.abcdefghi"));
		assertEquals("O email deve ter no máximo 50 caracteres", exception.getMessage());
	}

	@Test
	void setEmailShouldFailWhenEmailHasNoAtSymbol() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setEmail("mariateste.com"));
		assertEquals("O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e outro domínio)",
				exception.getMessage());
	}

	@Test
	void setEmailShouldFailWhenEmailHasNoDotAfterAt() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setEmail("maria@testecom"));
		assertEquals("O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e outro domínio)",
				exception.getMessage());
	}

	@Test
	void setEmailShouldFailWhenEmailEndsWithDot() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setEmail("maria@testecom."));
		assertEquals("O email deve seguir o formato mínimo: a@a.a (deve ter um usuário, arroba, domínio, ponto e outro domínio)",
				exception.getMessage());
	}

	@Test
	void setEmailShouldPassWhenEmailHasMinimumFormat() {
		assertDoesNotThrow(() -> userBusinessRule.setEmail("a@b.cc"));
		assertEquals("a@b.cc", userBusinessRule.getEmail());
	}

	@Test
	void setEmailShouldPassWhenTldHasOneCharacter() {
		assertDoesNotThrow(() -> userBusinessRule.setEmail("a@b.c"));
		assertEquals("a@b.c", userBusinessRule.getEmail());
	}

	@Test
	void setEmailShouldPassWhenEmailHasSubdomainAndDomain() {
		assertDoesNotThrow(() -> userBusinessRule.setEmail("user@subdomain.domain.com"));
		assertEquals("user@subdomain.domain.com", userBusinessRule.getEmail());
	}

	@Test
	void setEmailShouldPassWhenEmailHasFiftyChars() {
		String emailWithFiftyChars = "abcdefghijklmnopqrst@abcdefghijklmnopqrst.abcdefgh";
		assertDoesNotThrow(() -> userBusinessRule.setEmail(emailWithFiftyChars));
		assertEquals(emailWithFiftyChars, userBusinessRule.getEmail());
	}

	@Test
	void setEmailShouldTrimWhitespace() {
		assertDoesNotThrow(() -> userBusinessRule.setEmail("  maria@teste.com  "));
		assertEquals("maria@teste.com", userBusinessRule.getEmail());
	}





	// ===== Nickname =====
	@Test
	void setNicknameShouldPassWithValidNickname() {
		assertDoesNotThrow(() -> userBusinessRule.setNickname("maria"));
		assertEquals("maria", userBusinessRule.getNickname());
	}

	@Test
	void setNicknameShouldFailWhenNicknameIsNull() {
		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userBusinessRule.setNickname(null));
		assertEquals("A propriedade 'nickname' deve existir.", exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameIsEmpty() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname(""));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameHasLessThanFourChars() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname("abc"));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameStartsWithNumber() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname("1maria"));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameStartsWithSpace() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname(" maria"));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameContainsUppercase() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname("mariaSilva"));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameContainsSpace() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname("maria silva"));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameContainsSpecialChar() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname("maria_"));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldFailWhenNicknameHasMoreThanTwentyChars() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setNickname("abcdefghijklmnopqrstu"));
		assertEquals(
				"O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
				exception.getMessage());
	}

	@Test
	void setNicknameShouldPassWhenNicknameHasNumbers() {
		assertDoesNotThrow(() -> userBusinessRule.setNickname("maria123"));
		assertEquals("maria123", userBusinessRule.getNickname());
	}

	@Test
	void setNicknameShouldPassWhenNicknameHasMinimumLength() {
		assertDoesNotThrow(() -> userBusinessRule.setNickname("a1b2"));
		assertEquals("a1b2", userBusinessRule.getNickname());
	}

	@Test
	void setNicknameShouldPassWhenNicknameHasTwentyChars() {
		assertDoesNotThrow(() -> userBusinessRule.setNickname("a1b2c3d4e5f6g7h8i9j"));
		assertEquals("a1b2c3d4e5f6g7h8i9j", userBusinessRule.getNickname());
	}

	// ===== Age =====
	@Test
	void setAgeShouldPassWithValidAge() {
		assertDoesNotThrow(() -> userBusinessRule.setAge(25));
		assertEquals(25, userBusinessRule.getAge());
	}

	@Test
	void setAgeShouldFailWhenAgeIsNull() {
		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userBusinessRule.setAge(null));
		assertEquals("A propriedade 'age' deve existir.", exception.getMessage());
	}

	@Test
	void setAgeShouldFailWhenAgeIsZero() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setAge(0));
		assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
	}

	@Test
	void setAgeShouldFailWhenAgeIsNegative() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setAge(-5));
		assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
	}

	@Test
	void setAgeShouldFailWhenAgeIsGreaterThanOneHundredTwenty() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setAge(121));
		assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
	}

	@Test
	void setAgeShouldPassWhenAgeIsLowerBoundary() {
		assertDoesNotThrow(() -> userBusinessRule.setAge(1));
		assertEquals(1, userBusinessRule.getAge());
	}

	@Test
	void setAgeShouldPassWhenAgeIsUpperBoundary() {
		assertDoesNotThrow(() -> userBusinessRule.setAge(120));
		assertEquals(120, userBusinessRule.getAge());
	}

	// ===== Password =====
	@Test
	void setPasswordShouldPassWithValidPassword() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Abc123!"));
		assertEquals("Abc123!", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldFailWhenPasswordIsNull() {
		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userBusinessRule.setPassword(null));
		assertEquals("A propriedade 'password' deve existir.", exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordIsEmpty() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword(""));
		assertEquals("A senha deve ter entre 6 e 20 caracteres", exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordLengthIsTooShort() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("Ab1!"));
		assertEquals("A senha deve ter entre 6 e 20 caracteres", exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordLengthIsTooLong() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("AbcdefghijK1234!mnopq"));
		assertEquals("A senha deve ter entre 6 e 20 caracteres", exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordLacksUppercase() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("abc123!"));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordLacksLowercase() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("ABC123!"));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordLacksNumber() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("Abcdef!"));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordLacksSpecialChar() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("Abc1234"));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenPasswordHasNotAllowedSpecialChar() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("Abc123*"));
		assertEquals(
				"A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	@Test
	void setPasswordShouldPassWhenPasswordHasMinimumLength() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Ab1!cd"));
		assertEquals("Ab1!cd", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWhenPasswordHasMaximumLength() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("AbcdefghijK1234!mnop"));
		assertEquals("AbcdefghijK1234!mnop", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWithAtSymbol() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Abc123@"));
		assertEquals("Abc123@", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWithHashSymbol() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Abc123#"));
		assertEquals("Abc123#", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWithDollarSymbol() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Abc123$"));
		assertEquals("Abc123$", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWithPercentSymbol() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Abc123%"));
		assertEquals("Abc123%", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWithUnderscoreSymbol() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Abc123_"));
		assertEquals("Abc123_", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWithDashSymbol() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Abc123-"));
		assertEquals("Abc123-", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldPassWhenPasswordIsNewAndValid() {
		assertDoesNotThrow(() -> userBusinessRule.setPassword("Def456@", true));
		assertEquals("Def456@", userBusinessRule.getPassword());
	}

	@Test
	void setPasswordShouldFailWhenNewPasswordIsNull() {
		RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
				() -> userBusinessRule.setPassword(null, true));
		assertEquals("A propriedade 'password' deve existir.", exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenNewPasswordLengthIsTooShort() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("Ab1!", true));
		assertEquals("A nova senha deve ter entre 6 e 20 caracteres", exception.getMessage());
	}

	@Test
	void setPasswordShouldFailWhenNewPasswordLacksAllowedComposition() {
		RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
				() -> userBusinessRule.setPassword("abcdef1", true));
		assertEquals(
				"A nova senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
				exception.getMessage());
	}

	// ===== Other Fields =====
	@Test
	void setIdShouldWork() {
		assertDoesNotThrow(() -> userBusinessRule.setId(1L));
		assertEquals(1L, userBusinessRule.getId());
	}

	@Test
	void setLoginAttemptsShouldWork() {
		assertDoesNotThrow(() -> userBusinessRule.setLoginAttempts(3));
		assertEquals(3, userBusinessRule.getLoginAttempts());
	}

	@Test
	void setCreatedAtShouldWork() {
		LocalDateTime now = LocalDateTime.now();
		assertDoesNotThrow(() -> userBusinessRule.setCreatedAt(now));
		assertEquals(now, userBusinessRule.getCreatedAt());
	}
}
