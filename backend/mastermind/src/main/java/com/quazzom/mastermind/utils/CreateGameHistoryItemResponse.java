package com.quazzom.mastermind.utils;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.dto.GameHistoryItemResponse;
import com.quazzom.mastermind.entity.Game;

@Component
public class CreateGameHistoryItemResponse {

	public CalcGamesPoints calcGamePoints;

	public CreateGameHistoryItemResponse() {
		this(new CalcGamesPoints());
	}

	public CreateGameHistoryItemResponse(CalcGamesPoints calcGamePoints) {
		this.calcGamePoints = calcGamePoints;
	}

    public GameHistoryItemResponse calculatePoints(Game game) {

		int points = calcGamePoints.calculatePoints(game);

        GameHistoryItemResponse item = new GameHistoryItemResponse(
                game.getUuidPublic(),
				game.getLevel(),
				points,
				game.getStatus(),
				game.getAttemptsUsed(),
				game.getCreatedAt(),
				game.getFinishedAt());

        return item;
    }

}
