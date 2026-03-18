package com.quazzom.mastermind.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quazzom.mastermind.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
}