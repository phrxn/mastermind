import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { RankingService } from './ranking.service';

const BASE = 'http://localhost:8080/api/v1';

const BOARD_RESPONSE = {
  status: 'GAME_WIN',
  gameLevel: 2,
  numberOfColumnColors: 6,
  maximumOfattempts: 10,
  repeatedColorAllowed: false,
  rows: []
};

describe('RankingService', () => {
  let service: RankingService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(RankingService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  describe('getRanking()', () => {
    it('should call GET ranking', () => {
      service.getRanking().subscribe();

      const req = httpController.expectOne(`${BASE}/ranking`);
      expect(req.request.method).toBe('GET');
      req.flush({
        top10EasyGames: [],
        top10NormalGames: [],
        top10HardGames: [],
        top10MastermindGames: []
      });
    });

    it('should return a normalized ranking response', () => {
      let result: unknown;
      service.getRanking().subscribe((value) => {
        result = value;
      });

      httpController.expectOne(`${BASE}/ranking`).flush({
        top10EasyGamesList: [
          { gameUuidPublic: 'g1', userNickname: 'Ace', gameLevel: 1, pointsMaked: 99, attemptsUsed: 1 }
        ],
        top10NormalGamesList: [],
        top10HardGamesList: [],
        top10MastermindGamesList: []
      });

      const typed = result as { top10EasyGames: { userNickname: string; gameLevel: string }[] };
      expect(typed.top10EasyGames).toHaveSize(1);
      expect(typed.top10EasyGames[0].userNickname).toBe('Ace');
      expect(typed.top10EasyGames[0].gameLevel).toBe('EASY');
    });
  });

  describe('getRankingDetail()', () => {
    it('should call GET ranking/:uuid', () => {
      service.getRankingDetail('game-uuid-1').subscribe();

      const req = httpController.expectOne(`${BASE}/ranking/game-uuid-1`);
      expect(req.request.method).toBe('GET');
      req.flush(BOARD_RESPONSE);
    });

    it('should return a normalized board state', () => {
      let board: unknown;
      service.getRankingDetail('game-uuid-1').subscribe((value) => {
        board = value;
      });

      httpController.expectOne(`${BASE}/ranking/game-uuid-1`).flush(BOARD_RESPONSE);

      expect((board as { status: string }).status).toBe('WON');
      expect((board as { gameLevel: string }).gameLevel).toBe('NORMAL');
    });
  });
});
