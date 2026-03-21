package com.quazzom.mastermind.dto;

import java.util.List;

public class GameStatusResponse {

	private String status;
	private Integer gameLevel;
	private List<GameStatusColumnResponse> columns;

	public GameStatusResponse(String status, Integer gameLevel, List<GameStatusColumnResponse> columns) {
		this.status = status;
		this.gameLevel = gameLevel;
		this.columns = columns;
	}

	public String getStatus() {
		return status;
	}

	public Integer getGameLevel() {
		return gameLevel;
	}

	public List<GameStatusColumnResponse> getColumns() {
		return columns;
	}
}
