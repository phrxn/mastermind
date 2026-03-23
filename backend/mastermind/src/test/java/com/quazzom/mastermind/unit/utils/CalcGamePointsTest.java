package com.quazzom.mastermind.unit.utils;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.utils.CalcGamesPoints;

class CalcGamePointsTest {

    private CalcGamesPoints calcGamePoints;

    @BeforeEach
    void setUp() {
        calcGamePoints = new CalcGamesPoints();
    }

    @Test
    void testCalculatePointsGameWonLevelEasyAttempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.WON);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        int result = calcGamePoints.calculatePoints(game);

        assertEquals(100, result);

    }

    @Test
    void testCalculatePointsGameWonLevelNormalAttempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.NORMAL);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.WON);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        int result = calcGamePoints.calculatePoints(game);

        assertEquals(250, result);

    }

    @Test
    void testCalculatePointsGameWonLevelHardAttempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.NORMAL);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.WON);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        int result = calcGamePoints.calculatePoints(game);

        assertEquals(250, result);

    }

    @Test
    void testCalculatePointsGameWonLevelMastermindAttempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.MASTERMIND);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.WON);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        int result = calcGamePoints.calculatePoints(game);

        assertEquals(1563, result);

    }

    @Test
    void testCalculatePointsGameLostWhenStatusIsInProgress() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        int result = calcGamePoints.calculatePoints(game);

        assertEquals(0, result);
    }

    @Test
    void testCalculatePointsGameLostWhenStatusIsLOST() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.LOST);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        int result = calcGamePoints.calculatePoints(game);

        assertEquals(0, result);
    }

    @Test
    void testCalculatePointsGameLostWhenStatusIsGaveUp() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.GAVE_UP);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        int result = calcGamePoints.calculatePoints(game);

        assertEquals(0, result);
    }
}
