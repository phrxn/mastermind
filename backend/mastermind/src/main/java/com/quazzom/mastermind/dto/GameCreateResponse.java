package com.quazzom.mastermind.dto;

public class GameCreateResponse {

	private String status;
	private Integer gameLevel;

	public GameCreateResponse(String status, Integer gameLevel) {
		this.status = status;
		this.gameLevel = gameLevel;
	}

	public String getStatus() {
		return status;
	}

	public Integer getGameLevel() {
		return gameLevel;
	}
}
