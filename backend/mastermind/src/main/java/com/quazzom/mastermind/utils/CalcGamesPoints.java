package com.quazzom.mastermind.utils;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameStatus;

public class CalcGamesPoints {

    public int calculatePoints(Game game) {

        Integer points = 0;

        if (game.getStatus() == GameStatus.WON) {

            Integer level = game.getLevel().ordinal() + 1;
            Integer attempts = game.getAttemptsUsed();

            double levelWeight = Math.pow(2.5, level - 1);

            double attemptFactor = Math.pow((11 - attempts) / 10.0, 1.5);

            points = (int) Math.round(100 * levelWeight * attemptFactor);
        }

	return points;

    }

}
