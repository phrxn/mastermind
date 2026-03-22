package com.quazzom.mastermind.dto;

import java.util.List;

import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public class GameFullResponse {

    private final GameStatus status;
    private final GameLevel gameLevel;
    private final Integer numberOfColumnColors;
    private final Integer maximumOfattempts;
    private final boolean isRepeatedColorAllowed;
	private final List<Integer> secret;
    private final List<GameStatusRowResponse> row;

    public GameFullResponse(GameStatus status,
            GameLevel gameLevel,
            Integer numberOfColumnColors,
            Integer maximumOfattempts,
            boolean isRepeatedColorAllowed,
			List<Integer> secret,
            List<GameStatusRowResponse> row) {
        this.status = status;
        this.gameLevel = gameLevel;
        this.numberOfColumnColors = numberOfColumnColors;
        this.maximumOfattempts = maximumOfattempts;
        this.isRepeatedColorAllowed = isRepeatedColorAllowed;
		this.secret = secret;
        this.row = row;
    }

    public GameStatus getStatus() {
        return status;
    }

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    public Integer getNumberOfColumnColors() {
        return numberOfColumnColors;
    }

    public Integer getMaximumOfattempts() {
        return maximumOfattempts;
    }

    public boolean isRepeatedColorAllowed() {
        return isRepeatedColorAllowed;
	}

    public List<Integer> getSecret() {
        return secret;
    }

    public List<GameStatusRowResponse> getRows() {
        return row;
    }
}
