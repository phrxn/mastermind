package com.quazzom.mastermind.dto;

import com.quazzom.mastermind.businessrules.GameEngineResult;

public class GameInProgressResponse implements GameResponse {

	private String status;
	private Integer gameLevel;
	private GameEngineResult tips;

	public GameInProgressResponse(String status, Integer gameLevel, GameEngineResult tips) {
		this.status = status;
		this.gameLevel = gameLevel;
		this.tips = tips;
	}

	public String getStatus() {
		return status;
	}

	public Integer getGameLevel() {
		return gameLevel;
	}

	public GameEngineResult getTips() {
		return tips;
	}
}
