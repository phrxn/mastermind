package com.quazzom.mastermind.businessrules;

import java.util.List;

public class GameEngineResult {

	private final int correctPositions;
	private final int correctColors;
	private final List<Integer> tips;

	public GameEngineResult(int correctPositions, int correctColors, List<Integer> tips) {
		this.correctPositions = correctPositions;
		this.correctColors = correctColors;
		this.tips = tips;
	}

	public int getCorrectPositions() {
		return correctPositions;
	}

	public int getCorrectColors() {
		return correctColors;
	}

	public List<Integer> getTips() {
		return tips;
	}
}
