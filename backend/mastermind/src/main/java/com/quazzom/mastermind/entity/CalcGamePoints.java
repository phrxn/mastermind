package com.quazzom.mastermind.entity;

import com.quazzom.mastermind.dto.GameHistoryItemResponse;

public class CalcGamePoints {

    public GameHistoryItemResponse calculatePoints(Game game) {

        Integer points = 0;

        if (game.getStatus() == GameStatus.WON) {

            Integer level = game.getLevel().ordinal() + 1;
            Integer attempts = game.getAttemptsUsed();

            double levelWeight = Math.pow(2.5, level - 1);

            double attemptFactor = Math.pow((11 - attempts) / 10.0, 1.5);

            points = (int) Math.round(100 * levelWeight * attemptFactor);
        }

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
