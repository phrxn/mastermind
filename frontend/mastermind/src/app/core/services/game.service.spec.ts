import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { GameService } from './game.service';

const BASE = 'http://localhost:8080/api/v1';

const BOARD_RESPONSE = {
  status: 'GAME_IN_PROGRESS',
  gameLevel: 1,
  numberOfColumnColors: 6,
  maximumOfattempts: 10,
  repeatedColorAllowed: false,
  rows: []
};

describe('GameService', () => {
  let service: GameService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(GameService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  describe('getCurrentGame()', () => {
    it('should call GET game/status', () => {
      service.getCurrentGame().subscribe();

      const req = httpController.expectOne(`${BASE}/game/status`);
      expect(req.request.method).toBe('GET');
      req.flush(BOARD_RESPONSE);
    });

    it('should return null when status is 204', () => {
      let result: unknown = 'untouched';
      service.getCurrentGame().subscribe((value) => {
        result = value;
      });

      httpController.expectOne(`${BASE}/game/status`).flush(null, { status: 204, statusText: 'No Content' });

      expect(result).toBeNull();
    });

    it('should return a normalized board state when status is 200', () => {
      let board: unknown;
      service.getCurrentGame().subscribe((value) => {
        board = value;
      });

      httpController.expectOne(`${BASE}/game/status`).flush(BOARD_RESPONSE);

      expect(board).not.toBeNull();
      expect((board as { status: string }).status).toBe('IN_PROGRESS');
      expect((board as { gameLevel: string }).gameLevel).toBe('EASY');
    });
  });

  describe('createGame()', () => {
    it('should call POST game/create with the given level', () => {
      service.createGame('NORMAL').subscribe();

      const req = httpController.expectOne(`${BASE}/game/create`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ level: 'NORMAL' });
      req.flush(BOARD_RESPONSE);
    });

    it('should return a normalized board state', () => {
      let board: unknown;
      service.createGame('EASY').subscribe((value) => {
        board = value;
      });

      httpController.expectOne(`${BASE}/game/create`).flush(BOARD_RESPONSE);

      expect((board as { status: string }).status).toBe('IN_PROGRESS');
    });
  });

  describe('submitGuess()', () => {
    it('should call POST game/guess with the guess array', () => {
      service.submitGuess([1, 2, 3, 4]).subscribe();

      const req = httpController.expectOne(`${BASE}/game/guess`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ guess: [1, 2, 3, 4] });
      req.flush({ status: 'GAME_IN_PROGRESS', gameLevel: 1, tips: { correctPositions: 1, correctColors: 1 } });
    });

    it('should return a normalized guess outcome', () => {
      let outcome: unknown;
      service.submitGuess([1, 2, 3, 4]).subscribe((value) => {
        outcome = value;
      });

      httpController.expectOne(`${BASE}/game/guess`).flush({
        status: 'GAME_WIN',
        gameLevel: 2,
        tips: { correctPositions: 6, correctColors: 0 }
      });

      expect((outcome as { status: string }).status).toBe('WON');
      expect((outcome as { gameLevel: string }).gameLevel).toBe('NORMAL');
    });
  });

  describe('giveUp()', () => {
    it('should call POST game/give-up', () => {
      service.giveUp().subscribe();

      const req = httpController.expectOne(`${BASE}/game/give-up`);
      expect(req.request.method).toBe('POST');
      req.flush({ ...BOARD_RESPONSE, status: 'GAME_GIVE_UP' });
    });

    it('should return the normalized final board state', () => {
      let board: unknown;
      service.giveUp().subscribe((value) => {
        board = value;
      });

      httpController.expectOne(`${BASE}/game/give-up`).flush({ ...BOARD_RESPONSE, status: 'GAME_GIVE_UP' });

      expect((board as { status: string }).status).toBe('GAVE_UP');
    });
  });
});
