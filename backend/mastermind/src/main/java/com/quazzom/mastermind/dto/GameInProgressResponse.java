package com.quazzom.mastermind.dto;

import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public class GameInProgressResponse implements GameResponse {

	private GameStatus status;
	private GameLevel gameLevel;
	private GameEngineResult tips;

	public GameInProgressResponse(GameStatus status, GameLevel gameLevel, GameEngineResult tips) {
		this.status = status;
		this.gameLevel = gameLevel;
		this.tips = tips;
	}

	public GameStatus getStatus() {
		return status;
	}

	public GameLevel getGameLevel() {
		return gameLevel;
	}

	public GameEngineResult getTips() {
		return tips;
	}
}
