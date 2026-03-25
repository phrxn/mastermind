import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { HistoryService } from './history.service';

const BASE = 'http://localhost:8080/api/v1';

const BOARD_RESPONSE = {
  status: 'GAME_WIN',
  gameLevel: 1,
  numberOfColumnColors: 6,
  maximumOfattempts: 10,
  repeatedColorAllowed: false,
  rows: [{ guess: [1, 2, 3, 4], tips: { correctPositions: 4, correctColors: 0 } }]
};

describe('HistoryService', () => {
  let service: HistoryService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(HistoryService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  describe('getHistory()', () => {
    it('should call GET users/history', () => {
      service.getHistory().subscribe();

      const req = httpController.expectOne(`${BASE}/users/history`);
      expect(req.request.method).toBe('GET');
      req.flush({ gameHistoryBestGames: [], gameHistoryFull: [] });
    });

    it('should return a normalized history response', () => {
      let result: unknown;
      service.getHistory().subscribe((value) => {
        result = value;
      });

      httpController.expectOne(`${BASE}/users/history`).flush({
        gameHistoryBestGames: [
          { publicUuid: 'abc', level: 1, pointsMaked: 10, status: 1, attemptsUsed: 3 }
        ],
        gameHistoryFull: []
      });

      const typed = result as { gameHistoryBestGames: { level: string; status: string }[] };
      expect(typed.gameHistoryBestGames).toHaveSize(1);
      expect(typed.gameHistoryBestGames[0].level).toBe('EASY');
    });
  });

  describe('getHistoryDetail()', () => {
    it('should call GET users/history/:uuid', () => {
      service.getHistoryDetail('game-uuid-1').subscribe();

      const req = httpController.expectOne(`${BASE}/users/history/game-uuid-1`);
      expect(req.request.method).toBe('GET');
      req.flush(BOARD_RESPONSE);
    });

    it('should return a normalized board state', () => {
      let board: unknown;
      service.getHistoryDetail('game-uuid-1').subscribe((value) => {
        board = value;
      });

      httpController.expectOne(`${BASE}/users/history/game-uuid-1`).flush(BOARD_RESPONSE);

      expect((board as { status: string }).status).toBe('WON');
      expect((board as { rows: unknown[] }).rows).toHaveSize(1);
    });
  });
});
