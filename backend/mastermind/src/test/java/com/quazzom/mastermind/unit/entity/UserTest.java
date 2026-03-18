package com.quazzom.mastermind.unit.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.entity.User;

class UserTest {

    @Test
    void shouldSetAndGetAllFieldsIncludingDefaultLoginAttempts() {
        User user = new User();

        assertEquals(0, user.getLoginAttempts());

        user.setId(2L);
        user.setName("João");
        user.setEmail("joao@teste.com");
        user.setNickname("joao");
        user.setAge(30);
        user.setPassword("Abc123!");
        user.setLoginAttempts(3);

        assertEquals(2L, user.getId());
        assertEquals("João", user.getName());
        assertEquals("joao@teste.com", user.getEmail());
        assertEquals("joao", user.getNickname());
        assertEquals(30, user.getAge());
        assertEquals("Abc123!", user.getPassword());
        assertEquals(3, user.getLoginAttempts());
    }
}