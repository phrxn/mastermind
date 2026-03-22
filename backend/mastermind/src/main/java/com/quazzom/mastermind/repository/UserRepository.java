package com.quazzom.mastermind.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.quazzom.mastermind.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByNickname(String nickname);
	Optional<User> findByUuidPublic(UUID uuidPublic);

    @Modifying
    @Transactional
    @Query("""
        UPDATE User u
        SET u.name = :name,
            u.email = :email,
            u.nickname = :nickname,
            u.age = :age,
            u.password = :password
        WHERE u.id = :id
    """)
    int updateUserInfoById(
        @Param("id") Long id,
        @Param("name") String name,
        @Param("email") String email,
        @Param("nickname") String nickname,
        @Param("age") Integer age,
        @Param("password") String password
    );
}