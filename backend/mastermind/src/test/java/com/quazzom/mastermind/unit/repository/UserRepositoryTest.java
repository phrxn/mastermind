package com.quazzom.mastermind.unit.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.JpaRepository;

import com.quazzom.mastermind.repository.UserRepository;

class UserRepositoryTest {

    @Test
    void shouldExtendJpaRepositoryAndDeclareDerivedQueries() throws NoSuchMethodException {
        assertTrue(JpaRepository.class.isAssignableFrom(UserRepository.class));
        assertNotNull(UserRepository.class.getMethod("existsByEmail", String.class));
        assertNotNull(UserRepository.class.getMethod("existsByNickname", String.class));

        assertNotNull(
            UserRepository.class.getMethod(
                "updateUserInfoById",
                Long.class,
                String.class,
                String.class,
                String.class,
                Integer.class,
                String.class
            )
        );
    }
}