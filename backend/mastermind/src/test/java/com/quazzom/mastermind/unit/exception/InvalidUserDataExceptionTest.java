package com.quazzom.mastermind.unit.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.exception.InvalidUserDataException;

class InvalidUserDataExceptionTest {

    @Test
    void shouldSetMessageAndStatus400() {
        InvalidUserDataException exception = new InvalidUserDataException("Dado inválido");

        assertEquals("Dado inválido", exception.getMessage());
        assertEquals(400, exception.getStatus());
    }
}