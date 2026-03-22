package com.quazzom.mastermind.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public class GameHistoryItemResponse {

	private final UUID publicUuid;
	private final GameLevel level;
	private final Integer pointsMaked;
	private final GameStatus status;
	private final Integer attemptsUsed;
	private final LocalDateTime createdAt;
	private final LocalDateTime finishedAt;

	public GameHistoryItemResponse(UUID publicUuid, GameLevel level, Integer pointsMaked, GameStatus status, Integer attemptsUsed, LocalDateTime createdAt, LocalDateTime finishedAt) {
		this.publicUuid = publicUuid;
		this.level = level;
		this.pointsMaked = pointsMaked;
		this.status = status;
		this.attemptsUsed = attemptsUsed;

		this.createdAt = createdAt;
		this.finishedAt = finishedAt;
	}

	public UUID getPublicUuid() {
		return publicUuid;
	}

	public GameLevel getLevel() {
		return level;
	}

	public Integer getPointsMaked() {
		return pointsMaked;
	}

	public GameStatus getStatus() {
		return status;
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
