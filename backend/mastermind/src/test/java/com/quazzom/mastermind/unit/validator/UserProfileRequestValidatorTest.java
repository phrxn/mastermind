package com.quazzom.mastermind.unit.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.businessrules.UserBusinessRule;
import com.quazzom.mastermind.dto.UserProfileRequest;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.validator.UserProfileRequestValidator;

class UserProfileRequestValidatorTest {

    private UserProfileRequestValidator validator;

    @BeforeEach
    void setUp() {
        UserBusinessRule userBusinessRule = new UserBusinessRule("abc", "a@a.com", "abcd", 1, "Abc@1224");
        validator = new UserProfileRequestValidator(userBusinessRule);
    }

    // ===== validateRequestBody() =====

    @Test
    void validateRequestBodyShouldPassWithValidData() {
        UserProfileRequest request = validRequest();

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    // ===== Name =====

    @Test
    void validateRequestBodyShouldThrowWhenNameIsNull() {
        UserProfileRequest request = validRequest();
        request.setName(null);

        RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A propriedade 'name' deve existir.", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNameContainsNumber() {
        UserProfileRequest request = validRequest();
        request.setName("Maria1 Silva");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNameContainsSpecialChar() {
        UserProfileRequest request = validRequest();
        request.setName("Maria@Silva");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNameHasMoreThanSixtyChars() {
        UserProfileRequest request = validRequest();
        request.setName("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHI");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("O nome deve conter apenas letras e espaços, com no máximo 60 caracteres",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldPassWhenNameHasExactlySixtyChars() {
        UserProfileRequest request = validRequest();
        request.setName("ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGH");

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    // ===== Nickname =====

    @Test
    void validateRequestBodyShouldThrowWhenNicknameIsNull() {
        UserProfileRequest request = validRequest();
        request.setNickname(null);

        RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A propriedade 'nickname' deve existir.", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNicknameStartsWithUppercase() {
        UserProfileRequest request = validRequest();
        request.setNickname("Abcd");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNicknameHasLessThanFourChars() {
        UserProfileRequest request = validRequest();
        request.setNickname("abc");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNicknameHasMoreThanTwentyChars() {
        UserProfileRequest request = validRequest();
        request.setNickname("abcdefghijklmnopqrstu");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNicknameContainsSpecialChar() {
        UserProfileRequest request = validRequest();
        request.setNickname("abc_d");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "O nickname deve iniciar com letra minúscula, conter apenas letras minúsculas e números, e ter entre 4 e 20 caracteres",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldPassWhenNicknameHasExactlyFourChars() {
        UserProfileRequest request = validRequest();
        request.setNickname("abcd");

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    @Test
    void validateRequestBodyShouldPassWhenNicknameHasExactlyTwentyChars() {
        UserProfileRequest request = validRequest();
        request.setNickname("abcdefghijklmnopqrst");

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    // ===== Age =====

    @Test
    void validateRequestBodyShouldThrowWhenAgeIsNull() {
        UserProfileRequest request = validRequest();
        request.setAge(null);

        RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A propriedade 'age' deve existir.", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenAgeIsZero() {
        UserProfileRequest request = validRequest();
        request.setAge(0);

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenAgeIsNegative() {
        UserProfileRequest request = validRequest();
        request.setAge(-1);

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenAgeExceedsOneHundredTwenty() {
        UserProfileRequest request = validRequest();
        request.setAge(121);

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldPassWhenAgeIsOne() {
        UserProfileRequest request = validRequest();
        request.setAge(1);

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    @Test
    void validateRequestBodyShouldPassWhenAgeIsOneHundredTwenty() {
        UserProfileRequest request = validRequest();
        request.setAge(120);

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    private UserProfileRequest validRequest() {
        UserProfileRequest request = new UserProfileRequest();
        request.setName("Maria Silva");
        request.setNickname("maria123");
        request.setAge(25);
        return request;
    }
}
