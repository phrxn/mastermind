package com.quazzom.mastermind.dto;

import java.util.List;

public record GameHistoryResponse(
        List<GameHistoryItemResponse> gameHistoryBestGames,
        List<GameHistoryItemResponse> gameHistoryFull
) {
}