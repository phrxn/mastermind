package com.quazzom.mastermind.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameStatus;

public interface GameRepository extends JpaRepository<Game, Long> {

	List<Game> findByUserId(Long userId);

	Optional<Game> findFirstByUserIdAndStatusOrderByCreatedAtDesc(Long userId, GameStatus status);
}
