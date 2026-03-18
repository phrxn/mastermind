package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.UserAlreadyExistsException;

class UserAlreadyExistsExceptionTest {

    @Test
    void shouldSetMessageAndStatus409() {
        UserAlreadyExistsException exception = new UserAlreadyExistsException("Já existe");

        assertEquals("Já existe", exception.getMessage());
        assertEquals(409, exception.getStatus());
    }
}