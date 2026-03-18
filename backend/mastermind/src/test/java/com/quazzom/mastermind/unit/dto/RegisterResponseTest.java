package com.quazzom.mastermind.unit.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.dto.RegisterResponse;
import com.quazzom.mastermind.entity.User;

class RegisterResponseTest {

    @Test
    void constructorShouldMapUserFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Maria Silva");
        user.setEmail("maria@teste.com");
        user.setNickname("maria");
		user.setAge(18);

        RegisterResponse response = new RegisterResponse(user);

        assertEquals(1L, response.getId());
        assertEquals("Maria Silva", response.getName());
        assertEquals("maria@teste.com", response.getEmail());
        assertEquals("maria", response.getNickname());
        assertEquals(18, response.getAge());
    }
}