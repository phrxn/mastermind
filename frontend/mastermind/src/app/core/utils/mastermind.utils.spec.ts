import {
  buildTipsGrid,
  nextGuessSelection,
  normalizeBoardState,
  normalizeHistoryResponse,
  normalizeRankingResponse,
  normalizeStatus,
  removeGuessSelection
} from './mastermind.utils';

describe('mastermind utils', () => {
  it('should normalize board state with mixed backend naming', () => {
    const result = normalizeBoardState({
      status: 'GAME_IN_PROGRESS',
      gameLevel: 4,
      numberOfColumnColors: 6,
      maximumOfattempts: 10,
      repeatedColorAllowed: true,
      rows: [{ guess: [1, 2, 3, 4, 5, 6], tips: { correctPositions: 2, correctColors: 1 } }]
    });

    expect(result.status).toBe('IN_PROGRESS');
    expect(result.gameLevel).toBe('MASTERMIND');
    expect(result.maximumOfAttempts).toBe(10);
    expect(result.rows[0].tips.correctPositions).toBe(2);
  });

  it('should block duplicate colors when repetition is not allowed', () => {
    expect(nextGuessSelection([1, 2], 2, 4, false)).toEqual([1, 2]);
    expect(nextGuessSelection([1, 2], 3, 4, false)).toEqual([1, 2, 3]);
  });

  it('should remove a selected color by slot index', () => {
    expect(removeGuessSelection([1, 2, 3, 4], 1)).toEqual([1, 3, 4]);
  });

  it('should build a tips grid with trailing empty slots', () => {
    expect(buildTipsGrid({ correctPositions: 1, correctColors: 2 }, 4)).toEqual([
      'position',
      'color',
      'color',
      'empty'
    ]);
  });

  it('should normalize history status values from numeric responses', () => {
    const result = normalizeHistoryResponse({
      gameHistoryBestGames: [{ publicUuid: 'abc', level: 1, pointsMaked: 9, status: 2, attemptsUsed: 1 }],
      gameHistoryFull: []
    });

    expect(result.gameHistoryBestGames[0].level).toBe('EASY');
    expect(result.gameHistoryBestGames[0].status).toBe(normalizeStatus(2));
  });

  it('should normalize ranking payloads with list suffix keys', () => {
    const result = normalizeRankingResponse({
      top10EasyGamesList: [{ gameUuidPublic: 'game-1', userNickname: 'Ace', gameLevel: 1, pointsMaked: 30, attemptsUsed: 2 }],
      top10NormalGamesList: [],
      top10HardGamesList: [],
      top10MastermindGamesList: []
    });

    expect(result.top10EasyGames).toHaveSize(1);
    expect(result.top10EasyGames[0].gameLevel).toBe('EASY');
    expect(result.top10EasyGames[0].userNickname).toBe('Ace');
  });
});