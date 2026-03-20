package com.quazzom.mastermind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import com.quazzom.mastermind.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
}