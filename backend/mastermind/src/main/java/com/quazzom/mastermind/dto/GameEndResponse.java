package com.quazzom.mastermind.dto;

import java.util.List;

import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public record GameEndResponse(
        GameStatus status,
        GameLevel gameLevel,
        List<Integer> secret
) implements GameResponse {
}