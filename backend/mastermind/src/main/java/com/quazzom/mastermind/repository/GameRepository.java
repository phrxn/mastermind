package com.quazzom.mastermind.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByUserId(Long userId);

    Optional<Game> findFirstByUserIdAndStatusOrderByCreatedAtDesc(Long userId, GameStatus status);

	Optional<Game> findByUserIdAndUuidPublicAndStatusNot(Long userId, UUID uuidPublic, GameStatus status);

    @Query("""
		SELECT g
		FROM Game g
		WHERE g.user.id = :userId
          AND g.status <> com.quazzom.mastermind.entity.GameStatus.IN_PROGRESS
        ORDER BY g.createdAt DESC
    """)
    List<Game> findHistoryByUserId(@Param("userId") Long userId);

	@Query("""
		SELECT g
		FROM Game g
		WHERE g.user.id = :userId
		AND g.status = com.quazzom.mastermind.entity.GameStatus.WON

		AND g.attemptsUsed = (
			SELECT MIN(g2.attemptsUsed)
			FROM Game g2
			WHERE g2.user.id = :userId
				AND g2.level = g.level
				AND g2.status = com.quazzom.mastermind.entity.GameStatus.WON
		)

		AND g.createdAt = (
			SELECT MIN(g3.createdAt)
			FROM Game g3
			WHERE g3.user.id = :userId
				AND g3.level = g.level
				AND g3.status = com.quazzom.mastermind.entity.GameStatus.WON
				AND g3.attemptsUsed = g.attemptsUsed
		)

		ORDER BY g.level
	""")
	List<Game> findBestGamesPerLevel(@Param("userId") Long userId);

	List<Game> findTop10ByLevelAndStatusOrderByAttemptsUsedAscCreatedAtAsc(GameLevel level,GameStatus status);

	Optional<Game> findByUuidPublic(UUID uuidPublic);
}
