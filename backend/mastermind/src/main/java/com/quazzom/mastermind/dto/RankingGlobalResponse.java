package com.quazzom.mastermind.dto;

import java.util.List;

public record RankingGlobalResponse(
        List<RankingGlobalItemResponse> top10EasyGames,
        List<RankingGlobalItemResponse> top10NormalGames,
        List<RankingGlobalItemResponse> top10HardGames,
        List<RankingGlobalItemResponse> top10MastermindGames
) {
}