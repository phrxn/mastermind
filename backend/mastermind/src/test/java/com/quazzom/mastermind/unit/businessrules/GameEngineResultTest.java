package com.quazzom.mastermind.unit.businessrules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.businessrules.GameEngineResult;

class GameEngineResultTest {

    // ===== constructor / getCorrectPositions =====
    @Test
    void constructorShouldStoreCorrectPositions() {
        GameEngineResult result = new GameEngineResult(3, 1);

        assertEquals(3, result.getCorrectPositions());
    }

    // ===== constructor / getCorrectColors =====
    @Test
    void constructorShouldStoreCorrectColors() {
        GameEngineResult result = new GameEngineResult(3, 1);

        assertEquals(1, result.getCorrectColors());
    }

    // ===== edge values =====
    @Test
    void constructorShouldKeepZeroValues() {
        GameEngineResult result = new GameEngineResult(0, 0);

        assertEquals(0, result.getCorrectPositions());
        assertEquals(0, result.getCorrectColors());
    }

    @Test
    void constructorShouldKeepNegativeValuesAsProvided() {
        GameEngineResult result = new GameEngineResult(-1, -2);

        assertEquals(-1, result.getCorrectPositions());
        assertEquals(-2, result.getCorrectColors());
    }
}
