package com.quazzom.mastermind.dto;

import java.util.List;

public class GameGuessResponse {

	private String status;
	private Integer gameLevel;
	private List<Integer> tips;
	private List<Integer> secret;

	public GameGuessResponse(String status, Integer gameLevel, List<Integer> tips, List<Integer> secret) {
		this.status = status;
		this.gameLevel = gameLevel;
		this.tips = tips;
		this.secret = secret;
	}

	public String getStatus() {
		return status;
	}

	public Integer getGameLevel() {
		return gameLevel;
	}

	public List<Integer> getTips() {
		return tips;
	}

	public List<Integer> getSecret() {
		return secret;
	}
}
