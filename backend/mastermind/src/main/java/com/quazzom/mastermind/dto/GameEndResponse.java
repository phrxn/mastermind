package com.quazzom.mastermind.dto;

import java.util.List;

import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public class GameEndResponse implements GameResponse {

	private GameStatus status;
	private GameLevel gameLevel;
	private List<Integer> secret;

	public GameEndResponse(GameStatus status, GameLevel gameLevel, List<Integer> secret) {
		this.status = status;
		this.gameLevel = gameLevel;
		this.secret = secret;
	}

	public GameStatus getStatus() {
		return status;
	}

	public GameLevel getGameLevel() {
		return gameLevel;
	}

	public List<Integer> getSecret() {
		return secret;
	}
}
