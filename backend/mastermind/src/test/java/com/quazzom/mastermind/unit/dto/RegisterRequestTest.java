package com.quazzom.mastermind.unit.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.dto.RegisterRequest;

class RegisterRequestTest {

    @Test
    void shouldSetAndGetAllFields() {
        RegisterRequest request = new RegisterRequest();

        request.setName("Maria Silva");
        request.setEmail("maria@teste.com");
        request.setNickname("maria");
        request.setAge(25);
        request.setPassword("Abc123!");

        assertEquals("Maria Silva", request.getName());
        assertEquals("maria@teste.com", request.getEmail());
        assertEquals("maria", request.getNickname());
        assertEquals(25, request.getAge());
        assertEquals("Abc123!", request.getPassword());
    }
}