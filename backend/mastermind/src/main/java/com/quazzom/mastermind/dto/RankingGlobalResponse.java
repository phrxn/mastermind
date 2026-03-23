package com.quazzom.mastermind.dto;

import java.util.List;

public class RankingGlobalResponse {

    private final List<RankingGlobalItemResponse> top10EasyGames;
    private final List<RankingGlobalItemResponse> top10NormalGames;
    private final List<RankingGlobalItemResponse> top10HardGames;
    private final List<RankingGlobalItemResponse> top10MastermindGames;

    public RankingGlobalResponse(List<RankingGlobalItemResponse> top10EasyGamesList,
		List<RankingGlobalItemResponse> top10NormalGamesList,
		List<RankingGlobalItemResponse> top10HardGamesList,
		List<RankingGlobalItemResponse> top10MastermindGamesList) {
        this.top10EasyGames = top10EasyGamesList;
		this.top10NormalGames = top10NormalGamesList;
        this.top10HardGames = top10HardGamesList;
        this.top10MastermindGames = top10MastermindGamesList;
    }

	public List<RankingGlobalItemResponse> getTop10EasyGamesList() {
		return top10EasyGames;
	}

	public List<RankingGlobalItemResponse> getTop10NormalGamesList() {
		return top10NormalGames;
	}

	public List<RankingGlobalItemResponse> getTop10HardGamesList() {
		return top10HardGames;
	}

	public List<RankingGlobalItemResponse> getTop10MastermindGamesList() {
		return top10MastermindGames;
	}
}
