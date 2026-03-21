package com.quazzom.mastermind.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quazzom.mastermind.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {

	List<Game> findByUserId(Long userId);
}
