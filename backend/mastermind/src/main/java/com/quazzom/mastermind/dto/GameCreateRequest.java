package com.quazzom.mastermind.dto;

import com.quazzom.mastermind.entity.GameLevel;

public class GameCreateRequest {

	private GameLevel level;

	public GameLevel getLevel() {
		return level;
	}

	public void setLevel(GameLevel level) {
		this.level = level;
	}
}
