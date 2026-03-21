package com.quazzom.mastermind.dto;

import java.util.List;

public class GameStatusResponse {

    private final String status;
    private final Integer gameLevel;
    private final Integer numberOfColumnColors;
    private final Integer maximumOfattempts;
    private final boolean isRepeatedColorAllowed;
    private final List<GameStatusRowResponse> row;

    public GameStatusResponse(String status, Integer gameLevel, Integer numberOfColumnColors, Integer maximumOfattempts, boolean isRepeatedColorAllowed, List<GameStatusRowResponse> row) {
        this.status = status;
        this.gameLevel = gameLevel;
        this.numberOfColumnColors = numberOfColumnColors;
        this.maximumOfattempts = maximumOfattempts;
        this.isRepeatedColorAllowed = isRepeatedColorAllowed;
        this.row = row;
    }

    public String getStatus() {
        return status;
    }

    public Integer getGameLevel() {
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

    public List<GameStatusRowResponse> getRows() {
        return row;
    }
}
