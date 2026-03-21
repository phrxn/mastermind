package com.quazzom.mastermind.businessrules;

public class GameEngineResult {

	private final int correctPositions;
	private final int correctColors;

	public GameEngineResult(int correctPositions, int correctColors) {
		this.correctPositions = correctPositions;
		this.correctColors = correctColors;
	}

	public int getCorrectPositions() {
		return correctPositions;
	}

	public int getCorrectColors() {
		return correctColors;
	}
}
