package com.quazzom.mastermind.businessrules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.exception.GameFlowException;

@Component
public class GameEngine {

    public static final int COLOR_MIN = 1;
    public static final int COLOR_MAX = 6;
    public static final int MAX_ATTEMPTS = 10;
    public static final int TIP_CORRECT_POSITION = 1;
    public static final int TIP_CORRECT_COLOR = 2;

    private final Random random;

    public GameEngine() {
        this(new Random());
    }

    GameEngine(Random random) {
        this.random = random;
    }

    public List<Integer> createSecret(int codeLength, boolean allowDuplicates) {

        if (codeLength <= 0 || codeLength > 6) {
            throw new GameFlowException("Tamanho do código inválido");
        }

        List<Integer> secret = new ArrayList<>();
        Set<Integer> used = new HashSet<>();

        while (secret.size() < codeLength) {
            int color = random.nextInt(COLOR_MAX - COLOR_MIN + 1) + COLOR_MIN;
            if (!allowDuplicates && used.contains(color)) {
                continue;
            }
            secret.add(color);
            used.add(color);
        }

        return secret;
    }

    public void validateGuess(Game game, List<Integer> guess) {

        if (guess == null || guess.size() != game.getCodeLength()) {
            throw new GameFlowException(
                    String.format(
                        " A quantidade de cores na propriedade 'guess' não pode ser diferente da configurada no jogo. Quantidade esperada de cores: %d",
                        game.getCodeLength()
                    ));
        }

        for (Integer color : guess) {
            if (color == null || color < COLOR_MIN || color > COLOR_MAX) {
                throw new GameFlowException("A propriedade 'guess' contém valores de cor inválidos. As cores devem ser números entre 1 e 6");
            }
        }

        boolean hasRepeatedColors = new HashSet<>(guess).size() != guess.size();
        if (hasRepeatedColors && game.getLevel() != GameLevel.NORMAL && game.getLevel() != GameLevel.MASTERMIND) {
            throw new GameFlowException(
                    "A propriedade 'guess' contém valores de cores repetidas, mas o jogo atual não permite cores repetidas");
        }

    }

    public GameEngineResult evaluate(List<Integer> secret, List<Integer> guess) {
        if (secret == null || guess == null || secret.size() != guess.size()) {
            throw new GameFlowException("Não foi possível avaliar a tentativa");
        }

        int correctPositions = 0;
        Map<Integer, Integer> secretCount = new HashMap<>();
        Map<Integer, Integer> guessCount = new HashMap<>();

        for (int i = 0; i < secret.size(); i++) {
            Integer secretColor = secret.get(i);
            Integer guessColor = guess.get(i);

            if (secretColor.equals(guessColor)) {
                correctPositions++;
            } else {
                secretCount.put(secretColor, secretCount.getOrDefault(secretColor, 0) + 1);
                guessCount.put(guessColor, guessCount.getOrDefault(guessColor, 0) + 1);
            }
        }

        int correctColors = 0;
        for (Map.Entry<Integer, Integer> guessEntry : guessCount.entrySet()) {
            int secretOccurrences = secretCount.getOrDefault(guessEntry.getKey(), 0);
            correctColors += Math.min(secretOccurrences, guessEntry.getValue());
        }

        return new GameEngineResult(correctPositions, correctColors);
    }

    public boolean isWinning(GameEngineResult result, int codeLength) {
        return result.getCorrectPositions() == codeLength;
    }
}
