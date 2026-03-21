package com.quazzom.mastermind.dto;

import java.util.List;

public class GameGuessRequest {

	private List<Integer> guess;

	public List<Integer> getGuess() {
		return guess;
	}

	public void setGuess(List<Integer> guess) {
		this.guess = guess;
	}
}
