package com.quazzom.mastermind.dto;

import java.util.List;

import com.quazzom.mastermind.businessrules.GameEngineResult;

public class GameStatusRowResponse {

	private List<Integer> guess;
	private GameEngineResult tips;

	public GameStatusRowResponse(List<Integer> guess, GameEngineResult tips) {
		this.guess = guess;
		this.tips = tips;
	}

	public List<Integer> getGuess() {
		return guess;
	}

	public GameEngineResult getTips() {
		return tips;
	}
}
