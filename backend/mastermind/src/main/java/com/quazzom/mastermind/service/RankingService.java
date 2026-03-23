package com.quazzom.mastermind.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.quazzom.mastermind.businessrules.GameEngine;
import com.quazzom.mastermind.businessrules.GameEngineResult;
import com.quazzom.mastermind.dto.GameFullResponse;
import com.quazzom.mastermind.dto.GameStatusRowResponse;
import com.quazzom.mastermind.dto.RankingGlobalItemResponse;
import com.quazzom.mastermind.dto.RankingGlobalResponse;
import com.quazzom.mastermind.entity.Game;
import com.quazzom.mastermind.entity.GameLevel;
import com.quazzom.mastermind.entity.GameStatus;
import com.quazzom.mastermind.entity.Guess;
import com.quazzom.mastermind.exception.GameNotFoundException;
import com.quazzom.mastermind.repository.GameRepository;
import com.quazzom.mastermind.repository.GuessRepository;
import com.quazzom.mastermind.utils.CreateRankingGlobalItemResponse;
import com.quazzom.mastermind.utils.SecretDecoder;

@Service
public class RankingService {

    private final GameRepository gameRepository;
    private final GuessRepository guessRepository;
    private final CreateRankingGlobalItemResponse createRankingGlobalItemResponse;

    private final SecretDecoder secretDecoder;

    public RankingService(GameRepository gameRepository,
            GuessRepository guessRepository,
            CreateRankingGlobalItemResponse createRankingGlobalItemResponse,
            SecretDecoder secretDecoder
    ) {
        this.gameRepository = gameRepository;
        this.guessRepository = guessRepository;
        this.createRankingGlobalItemResponse = createRankingGlobalItemResponse;
        this.secretDecoder = secretDecoder;
    }

    public RankingGlobalResponse getGlobalRanking() {

        List<Game> top10EasyGamesList = getTop10GamesByLevel(GameLevel.EASY);
        List<Game> top10NormalGamesList = getTop10GamesByLevel(GameLevel.NORMAL);
        List<Game> top10HardGamesList = getTop10GamesByLevel(GameLevel.HARD);
        List<Game> top10MastermindGamesList = getTop10GamesByLevel(GameLevel.MASTERMIND);

        List<RankingGlobalItemResponse> top10EasyGamesResponseList
                = createRankingGlobalItemResponseList(top10EasyGamesList);
        List<RankingGlobalItemResponse> top10NormalGamesResponseList
                = createRankingGlobalItemResponseList(top10NormalGamesList);
        List<RankingGlobalItemResponse> top10HardGamesResponseList
                = createRankingGlobalItemResponseList(top10HardGamesList);
        List<RankingGlobalItemResponse> top10MastermindGamesResponseList
                = createRankingGlobalItemResponseList(top10MastermindGamesList);

        return new RankingGlobalResponse(top10EasyGamesResponseList, top10NormalGamesResponseList, top10HardGamesResponseList, top10MastermindGamesResponseList);

    }

    public GameFullResponse getRankedGameByUuidPublic(UUID gameUuid) {

        String errorMessage = "Jogo não encontrado. Ele não existe ou não está dentro de nenhum top 10";

        Optional<Game> gameOptional = gameRepository.findByUuidPublic(gameUuid);

        if (gameOptional.isEmpty()) {
            throw new GameNotFoundException(errorMessage);
        }

        Game game = gameOptional.get();

        List<Game> top10Games = getTop10GamesByLevel(game.getLevel());

        boolean isGameInTop10 = top10Games.stream().anyMatch(g -> g.getId().equals(game.getId()));

        if (!isGameInTop10) {
            throw new GameNotFoundException(errorMessage);
        }

        List<Guess> guesses = guessRepository.findByGameIdOrderByAttemptNumberAsc(game.getId());
        List<GameStatusRowResponse> rows = guesses.stream()
                .map(item
                        -> new GameStatusRowResponse(
                        secretDecoder.decode(item.getGuess()),
                        new GameEngineResult(item.getCorrectPositions(), item.getCorrectColors())
                ))
                .collect(Collectors.toList());

        GameFullResponse response = new GameFullResponse(
                game.getStatus(),
                game.getLevel(),
                game.getCodeLength(),
                GameEngine.MAX_ATTEMPTS,
                game.getAllowDuplicates(),
                secretDecoder.decode(game.getSecretCode()),
                rows);

        return response;
    }

    private List<RankingGlobalItemResponse> createRankingGlobalItemResponseList(List<Game> games) {
        return games.stream()
                .map(game -> createRankingGlobalItemResponse.calculatePoints(game))
                .toList();
    }

    /**
     * Search the top 10 games of a given level, ordered by attempts used and
     * then by creation date, and return them as a list. If there are less than
     * 10 games in that level, returns all the games in that level with the same
     * ordering.
     *
     * @param level the game level to search the top 10 games for
	 *
     * @return a list of the top 10 games for the specified level
     */
    private List<Game> getTop10GamesByLevel(GameLevel level) {
        return gameRepository.findTop10ByLevelAndStatusOrderByAttemptsUsedAscCreatedAtAsc(level, GameStatus.WON);
    }
}
