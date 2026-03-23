package com.quazzom.mastermind.dto;

import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public record GameInProgressResponse(
        GameStatus status,
        GameLevel gameLevel,
        GameEngineResult tips
) implements GameResponse {
}