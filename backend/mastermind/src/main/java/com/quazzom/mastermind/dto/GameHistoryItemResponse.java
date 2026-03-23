package com.quazzom.mastermind.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public record GameHistoryItemResponse(
        UUID publicUuid,
        GameLevel level,
        Integer pointsMaked,
        GameStatus status,
        Integer attemptsUsed,
        LocalDateTime createdAt,
        LocalDateTime finishedAt
) {
}