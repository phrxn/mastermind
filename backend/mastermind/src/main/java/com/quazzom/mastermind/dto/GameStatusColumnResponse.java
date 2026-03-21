package com.quazzom.mastermind.dto;

import java.util.List;

public class GameStatusColumnResponse {

	private List<Integer> guess;
	private List<Integer> tips;

	public GameStatusColumnResponse(List<Integer> guess, List<Integer> tips) {
		this.guess = guess;
		this.tips = tips;
	}

	public List<Integer> getGuess() {
		return guess;
	}

	public List<Integer> getTips() {
		return tips;
	}
}
