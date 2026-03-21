package com.quazzom.mastermind.dto;

import java.util.List;

public class GameGiveUpResponse {

	private String status;
	private Integer gameLevel;
	private List<Integer> secret;

	public GameGiveUpResponse(String status, Integer gameLevel, List<Integer> secret) {
		this.status = status;
		this.gameLevel = gameLevel;
		this.secret = secret;
	}

	public String getStatus() {
		return status;
	}

	public Integer getGameLevel() {
		return gameLevel;
	}

	public List<Integer> getSecret() {
		return secret;
	}
}
