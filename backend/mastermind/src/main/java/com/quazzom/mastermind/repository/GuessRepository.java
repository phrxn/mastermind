package com.quazzom.mastermind.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quazzom.mastermind.entity.Guess;

public interface GuessRepository extends JpaRepository<Guess, Long> {

	List<Guess> findByGameIdOrderByAttemptNumberAsc(Long gameId);
}
