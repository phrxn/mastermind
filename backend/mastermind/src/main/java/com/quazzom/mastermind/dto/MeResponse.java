package com.quazzom.mastermind.dto;

import java.util.UUID;

import com.quazzom.mastermind.entity.User;

public record MeResponse(
        boolean authenticated,
        UUID uuidPublic,
        String name,
        String email,
        String nickname,
        Integer age
) {
    public MeResponse(User user) {
        this(
            true,
            user.getUuidPublic(),
            user.getName(),
            user.getEmail(),
            user.getNickname(),
            user.getAge()
        );
    }
}