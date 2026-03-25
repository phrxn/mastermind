package com.quazzom.mastermind.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "games")
public class Game {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "uuid_public", nullable = false, unique = true, length = 36)
	@JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID uuidPublic;

	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	private GameLevel level;

	private Integer codeLength;
	private Boolean allowDuplicates;
	private String secretCode;

	@Enumerated(EnumType.STRING)
	private GameStatus status;

	private Integer attemptsUsed = 0;
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime finishedAt;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Guess> guesses = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUuidPublic(UUID uuidPublic) {
		this.uuidPublic = uuidPublic;
	}

	public UUID getUuidPublic() {
		return uuidPublic;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public GameLevel getLevel() {
		return level;
	}

	public void setLevel(GameLevel level) {
		this.level = level;
	}

	public Integer getCodeLength() {
		return codeLength;
	}

	public void setCodeLength(Integer codeLength) {
		this.codeLength = codeLength;
	}

	public Boolean getAllowDuplicates() {
		return allowDuplicates;
	}

	public void setAllowDuplicates(Boolean allowDuplicates) {
		this.allowDuplicates = allowDuplicates;
	}

	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}

	public GameStatus getStatus() {
		return status;
	}

	public void setStatus(GameStatus status) {
		this.status = status;
	}

	public Integer getAttemptsUsed() {
		return attemptsUsed;
	}

	public void setAttemptsUsed(Integer attemptsUsed) {
		this.attemptsUsed = attemptsUsed;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public List<Guess> getGuesses() {
		return guesses;
	}

	public void setGuesses(List<Guess> guesses) {
		this.guesses = guesses;
	}

	@PrePersist
	private void prePersist() {
		if (uuidPublic == null) {
			uuidPublic = UUID.randomUUID();
		}
	}

}
