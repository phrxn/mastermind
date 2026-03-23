package com.quazzom.mastermind.unit.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.dto.UserPasswordRequest;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;
import com.quazzom.mastermind.validator.UserPasswordRequestValidator;

class UserPasswordRequestValidatorTest {

    private UserPasswordRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new UserPasswordRequestValidator();
    }

    // ===== validateRequestBody() =====

    @Test
    void validateRequestBodyShouldPassWithValidData() {
        UserPasswordRequest request = validRequest();

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    @Test
    void validateRequestBodyShouldThrowWhenCurrentPasswordIsNull() {
        UserPasswordRequest request = validRequest();
        request.setCurrentPassword(null);

        RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A propriedade 'currentPassword' deve existir.", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNewPasswordIsNull() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword(null);

        RequestPropertyNotFoundException exception = assertThrows(RequestPropertyNotFoundException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A propriedade 'newPassword' deve existir.", exception.getMessage());
    }

    // ===== isCurrentPasswordValid() =====

    @Test
    void validateRequestBodyShouldThrowWhenCurrentPasswordIsEmpty() {
        UserPasswordRequest request = validRequest();
        request.setCurrentPassword("");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("O campo 'currentPassword' deve ser preenchido, ele não pode estar vazio",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenCurrentPasswordHasOnlySpaces() {
        UserPasswordRequest request = validRequest();
        request.setCurrentPassword("   ");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("O campo 'currentPassword' deve ser preenchido, ele não pode estar vazio",
                exception.getMessage());
    }

    // ===== isNewPasswordValid() =====

    @Test
    void validateRequestBodyShouldThrowWhenNewPasswordIsTooShort() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("Aa1!");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A nova senha deve ter entre 6 e 20 caracteres", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNewPasswordIsTooLong() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("Abc@1234567890123456789");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals("A nova senha deve ter entre 6 e 20 caracteres", exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNewPasswordHasNoUppercase() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("abc@1234");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "A nova senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNewPasswordHasNoLowercase() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("ABC@1234");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "A nova senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNewPasswordHasNoDigit() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("Abcdef@!");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "A nova senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldThrowWhenNewPasswordHasNoSymbol() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("Abcdef12");

        RequestInvalidPropertyValueException exception = assertThrows(RequestInvalidPropertyValueException.class,
                () -> validator.validateRequestBody(request));

        assertEquals(
                "A nova senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
                exception.getMessage());
    }

    @Test
    void validateRequestBodyShouldPassWhenNewPasswordHasExactlyMinimumLength() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("Aa1!bc");

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    @Test
    void validateRequestBodyShouldPassWhenNewPasswordHasExactlyMaximumLength() {
        UserPasswordRequest request = validRequest();
        request.setNewPassword("Aa1!bcdefghijklmnop");

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    private UserPasswordRequest validRequest() {
        UserPasswordRequest request = new UserPasswordRequest();
        request.setCurrentPassword("Abc@1234");
        request.setNewPassword("Xyz#5678");
        return request;
    }
}
