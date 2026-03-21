package com.quazzom.mastermind.unit.businessrules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.quazzom.mastermind.businessrules.GameBusinessRole;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.exception.RequestInvalidPropertyValueException;
import com.quazzom.mastermind.exception.RequestPropertyNotFoundException;

class GameBusinessRoleTest {

    private GameBusinessRole gameBusinessRole;

    @BeforeEach
    void setUp() {
        gameBusinessRole = new GameBusinessRole();
    }

    // ===== setLevel(GameLevel) =====
    @Test
    void setLevelGameLevelShouldSetEasy() {
        gameBusinessRole.setLevel(GameLevel.EASY);

        assertEquals(GameLevel.EASY, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelGameLevelShouldSetNormal() {
        gameBusinessRole.setLevel(GameLevel.NORMAL);

        assertEquals(GameLevel.NORMAL, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelGameLevelShouldSetHard() {
        gameBusinessRole.setLevel(GameLevel.HARD);

        assertEquals(GameLevel.HARD, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelGameLevelShouldSetMastermind() {
        gameBusinessRole.setLevel(GameLevel.MASTERMIND);

        assertEquals(GameLevel.MASTERMIND, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelGameLevelShouldThrowWhenLevelIsNull() {
        RequestPropertyNotFoundException exception = assertThrows(
                RequestPropertyNotFoundException.class,
                () -> gameBusinessRole.setLevel((GameLevel) null));

        assertEquals("A propriedade 'level' deve existir.", exception.getMessage());
    }

    // ===== setLevel(Integer) =====
    @Test
    void setLevelIntegerShouldMapOneToEasy() {
        gameBusinessRole.setLevel(1);

        assertEquals(GameLevel.EASY, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelIntegerShouldMapTwoToNormal() {
        gameBusinessRole.setLevel(2);

        assertEquals(GameLevel.NORMAL, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelIntegerShouldMapThreeToHard() {
        gameBusinessRole.setLevel(3);

        assertEquals(GameLevel.HARD, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelIntegerShouldMapFourToMastermind() {
        gameBusinessRole.setLevel(4);

        assertEquals(GameLevel.MASTERMIND, gameBusinessRole.getLevel());
    }

    @Test
    void setLevelIntegerShouldThrowWhenLevelIsNull() {
        RequestPropertyNotFoundException exception = assertThrows(
                RequestPropertyNotFoundException.class,
                () -> gameBusinessRole.setLevel((Integer) null));

        assertEquals("A propriedade 'level' deve existir.", exception.getMessage());
    }

    @Test
    void setLevelIntegerShouldThrowWhenLevelIsLessThanOne() {
        RequestInvalidPropertyValueException exception = assertThrows(
                RequestInvalidPropertyValueException.class,
                () -> gameBusinessRole.setLevel(0));

        assertEquals("O nível deve ser um valor entre 1 e 4", exception.getMessage());
    }

    @Test
    void setLevelIntegerShouldThrowWhenLevelIsGreaterThanFour() {
        RequestInvalidPropertyValueException exception = assertThrows(
                RequestInvalidPropertyValueException.class,
                () -> gameBusinessRole.setLevel(5));

        assertEquals("O nível deve ser um valor entre 1 e 4", exception.getMessage());
    }

    // ===== setGuess(List<Integer>) =====
    @Test
    void setGuessShouldThrowWhenGuessIsNull() {
        RequestPropertyNotFoundException exception = assertThrows(
                RequestPropertyNotFoundException.class,
                () -> gameBusinessRole.setGuess(null));

        assertEquals("A propriedade 'guess' deve existir.", exception.getMessage());
    }

    @Test
    void setGuessShouldThrowWhenGuessHasThreeItems() {
        RequestInvalidPropertyValueException exception = assertThrows(
                RequestInvalidPropertyValueException.class,
                () -> gameBusinessRole.setGuess(List.of(1, 2, 3)));

        assertEquals("A quantidade de cores na propriedade 'guess' deve ser 4 ou 6", exception.getMessage());
    }

    @Test
    void setGuessShouldThrowWhenGuessHasFiveItems() {
        RequestInvalidPropertyValueException exception = assertThrows(
                RequestInvalidPropertyValueException.class,
                () -> gameBusinessRole.setGuess(List.of(1, 2, 3, 4, 5)));

        assertEquals("A quantidade de cores na propriedade 'guess' deve ser 4 ou 6", exception.getMessage());
    }

    @Test
    void setGuessShouldAcceptWhenGuessHasFourItems() {
        assertDoesNotThrow(() -> gameBusinessRole.setGuess(List.of(1, 2, 3, 4)));
    }

    @Test
    void setGuessShouldAcceptWhenGuessHasSixItems() {
        assertDoesNotThrow(() -> gameBusinessRole.setGuess(List.of(1, 2, 3, 4, 5, 6)));
    }
}
