package com.quazzom.mastermind.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.quazzom.mastermind.entity.GameLevel;

public class RankingGlobalItemResponse {

	private final UUID gameUuidPublic;
	private final String userNickname;
	private final GameLevel gameLevel;
	private final Integer pointsMaked;
	private final Integer attemptsUsed;
	private final LocalDateTime createdAt;
	private final LocalDateTime finishedAt;

    public RankingGlobalItemResponse(UUID gameUuidPublic, String userNickname, GameLevel gameLevel, Integer pointsMaked, Integer attemptsUsed, LocalDateTime createdAt, LocalDateTime finishedAt) {
		this.gameUuidPublic = gameUuidPublic;
		this.userNickname = userNickname;
		this.gameLevel = gameLevel;
		this.pointsMaked = pointsMaked;
		this.attemptsUsed = attemptsUsed;
		this.createdAt = createdAt;
		this.finishedAt = finishedAt;
	}

    public UUID getGameUuidPublic() {
        return gameUuidPublic;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public GameLevel getGameLevel() {
        return gameLevel;
    }

    public Integer getPointsMaked() {
        return pointsMaked;
    }

    public Integer getAttemptsUsed() {
        return attemptsUsed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }



}
