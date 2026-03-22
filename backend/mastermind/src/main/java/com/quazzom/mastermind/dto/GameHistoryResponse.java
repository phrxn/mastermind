package com.quazzom.mastermind.dto;

import java.util.List;

public class GameHistoryResponse {

	private final List<GameHistoryItemResponse> gameHistoryBestGames;
	private final List<GameHistoryItemResponse> gameHistoryFull;

	public GameHistoryResponse(List<GameHistoryItemResponse> gameHistoryBestGames, List<GameHistoryItemResponse> gameHistoryFull) {
		this.gameHistoryBestGames = gameHistoryBestGames;
		this.gameHistoryFull = gameHistoryFull;
	}

	public List<GameHistoryItemResponse> getGameHistoryBestGames() {
		return gameHistoryBestGames;
	}

	public List<GameHistoryItemResponse> getGameHistoryFull() {
		return gameHistoryFull;
	}
}
