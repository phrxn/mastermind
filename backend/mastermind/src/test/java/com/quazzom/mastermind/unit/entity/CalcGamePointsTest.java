package com.quazzom.mastermind.unit.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.dto.GameHistoryItemResponse;
import com.quazzom.mastermind.entity.CalcGamePoints;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;

class CalcGamePointsTest {

    private CalcGamePoints calcGamePoints;

    @BeforeEach
    void setUp() {
        calcGamePoints = new CalcGamePoints();
    }

    @Test
    void testCalculatePointsGameWonLevel1Attempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.WON);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        GameHistoryItemResponse result = calcGamePoints.calculatePoints(game);

        assertEquals(100, result.getPointsMaked());
        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(GameLevel.EASY, result.getLevel());
        assertEquals(game.getAttemptsUsed(), result.getAttemptsUsed());
    }

    @Test
    void testCalculatePointsGameLostLevel1Attempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.LOST);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        GameHistoryItemResponse result = calcGamePoints.calculatePoints(game);

        assertEquals(0, result.getPointsMaked());
        assertEquals(GameStatus.LOST, result.getStatus());
        assertEquals(GameLevel.EASY, result.getLevel());
        assertEquals(game.getAttemptsUsed(), result.getAttemptsUsed());
    }

    @Test
    void testCalculatePointsGameGaveUpLevel1Attempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.GAVE_UP);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        GameHistoryItemResponse result = calcGamePoints.calculatePoints(game);

        assertEquals(0, result.getPointsMaked());
        assertEquals(GameStatus.GAVE_UP, result.getStatus());
        assertEquals(GameLevel.EASY, result.getLevel());
        assertEquals(game.getAttemptsUsed(), result.getAttemptsUsed());
    }

	@Test
    void testCalculatePointsGameInProgressLevel1Attempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.EASY);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        GameHistoryItemResponse result = calcGamePoints.calculatePoints(game);

        assertEquals(0, result.getPointsMaked());
        assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
        assertEquals(GameLevel.EASY, result.getLevel());
        assertEquals(game.getAttemptsUsed(), result.getAttemptsUsed());
    }

	@Test
    void testCalculatePointsGameInProgressLevel4Attempts1() {
        Game game = new Game();
        game.setLevel(GameLevel.MASTERMIND);
        game.setAttemptsUsed(1);
        game.setStatus(GameStatus.WON);
        game.setUuidPublic(UUID.randomUUID());
        game.setCreatedAt(LocalDateTime.now());
        game.setFinishedAt(LocalDateTime.now());

        GameHistoryItemResponse result = calcGamePoints.calculatePoints(game);

        assertEquals(1563, result.getPointsMaked());
        assertEquals(GameStatus.WON, result.getStatus());
        assertEquals(GameLevel.MASTERMIND, result.getLevel());
        assertEquals(game.getAttemptsUsed(), result.getAttemptsUsed());
    }
}
