package com.quazzom.mastermind.dto;

import java.util.List;

import com.quazzom.mastermind.businessrules.GameEngineResult;

public record GameStatusRowResponse(
        List<Integer> guess,
        GameEngineResult tips
) {
}