package com.quazzom.mastermind.utils;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.dto.RankingGlobalItemResponse;
import com.quazzom.mastermind.entity.Game;

@Component
public class CreateRankingGlobalItemResponse {

	public CalcGamesPoints calcGamePoints;

	public CreateRankingGlobalItemResponse() {
		this(new CalcGamesPoints());
	}

	public CreateRankingGlobalItemResponse(CalcGamesPoints calcGamePoints) {
		this.calcGamePoints = calcGamePoints;
	}

    public RankingGlobalItemResponse calculatePoints(Game game) {

		int points = calcGamePoints.calculatePoints(game);

        RankingGlobalItemResponse item = new RankingGlobalItemResponse(
                game.getUuidPublic(),
				game.getUser().getNickname(),
				game.getLevel(),
				points,
				game.getAttemptsUsed(),
				game.getCreatedAt(),
				game.getFinishedAt());

        return item;
    }

}
