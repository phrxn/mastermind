package com.quazzom.mastermind.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class GameHistoryItemResponse {

	private final UUID publicUuid;
	private final Integer level;
	private final Integer pointsMaked;
	private final Integer status;
	private final Integer attemptsUsed;
	private final LocalDateTime createdAt;
	private final LocalDateTime finishedAt;

	public GameHistoryItemResponse(UUID publicUuid, Integer level, Integer pointsMaked, Integer status, Integer attemptsUsed, LocalDateTime createdAt, LocalDateTime finishedAt) {
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

	public Integer getLevel() {
		return level;
	}

	public Integer getPointsMaked() {
		return pointsMaked;
	}

	public Integer getStatus() {
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
