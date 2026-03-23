package com.quazzom.mastermind.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.quazzom.mastermind.entity.GameLevel;

public record RankingGlobalItemResponse(
        UUID gameUuidPublic,
        String userNickname,
        GameLevel gameLevel,
        Integer pointsMaked,
        Integer attemptsUsed,
        LocalDateTime createdAt,
        LocalDateTime finishedAt
) {
}