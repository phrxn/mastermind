package com.quazzom.mastermind.unit.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.dto.RegisterRequest;
import com.quazzom.mastermind.exception.InvalidUserDataException;
import com.quazzom.mastermind.validator.UserValidator;

class UserValidatorTest {

    private UserValidator userValidator;

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
    }

    @Test
    void validateRegisterShouldPassWithValidData() {
        RegisterRequest request = validRequest();

        assertDoesNotThrow(() -> userValidator.validateRegister(request));
    }

    @Test
    void validateRegisterShouldFailWhenNameIsNull() {
        RegisterRequest request = validRequest();
        request.setName(null);

        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class,
                () -> userValidator.validateRegister(request));
        assertEquals("A propriedade 'nome' deve existir.", exception.getMessage());
    }

    @Test
    void validateRegisterShouldFailWhenNicknameIsInvalid() {
        RegisterRequest request = validRequest();
        request.setNickname("NickMaiusculo");

        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class,
            () -> userValidator.validateRegister(request));
        assertEquals("O nickname deve conter apenas letras minúsculas, com no máximo 20 caracteres",
            exception.getMessage());
    }

    @Test
    void validateRegisterShouldFailWhenAgeIsOutOfRange() {
        RegisterRequest request = validRequest();
        request.setAge(0);

        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class,
                () -> userValidator.validateRegister(request));
        assertEquals("A idade deve ser um número entre 1 e 120", exception.getMessage());
    }

    @Test
    void validateRegisterShouldFailWhenPasswordLacksUppercase() {
        RegisterRequest request = validRequest();
        request.setPassword("abc123!");

        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class,
            () -> userValidator.validateRegister(request));
        assertEquals(
            "A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
            exception.getMessage());
    }

    @Test
    void validateRegisterShouldFailWhenPasswordLacksLowercase() {
        RegisterRequest request = validRequest();
        request.setPassword("ABC123!");

        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class,
            () -> userValidator.validateRegister(request));
        assertEquals(
            "A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
            exception.getMessage());
    }

    @Test
    void validateRegisterShouldFailWhenPasswordLacksNumber() {
        RegisterRequest request = validRequest();
        request.setPassword("Abcdef!");

        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class,
            () -> userValidator.validateRegister(request));
        assertEquals(
            "A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
            exception.getMessage());
    }

    @Test
    void validateRegisterShouldFailWhenPasswordLacksSpecialChar() {
        RegisterRequest request = validRequest();
        request.setPassword("Abc1234");

        InvalidUserDataException exception = assertThrows(InvalidUserDataException.class,
            () -> userValidator.validateRegister(request));
        assertEquals(
            "A senha deve conter letra maiúscula, letra minúscula, número e ao menos um símbolo entre: ! @ # $ % ¨ _ -",
            exception.getMessage());
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