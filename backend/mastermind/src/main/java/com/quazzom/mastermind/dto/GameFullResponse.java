package com.quazzom.mastermind.dto;

import java.util.List;

import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public record GameFullResponse(
        GameStatus status,
        GameLevel gameLevel,
        Integer numberOfColumnColors,
        Integer maximumOfattempts,
        boolean repeatedColorAllowed,
        List<Integer> secret,
        List<GameStatusRowResponse> rows
) {
}