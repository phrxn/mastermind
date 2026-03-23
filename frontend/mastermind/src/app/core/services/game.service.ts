import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { GameBoardState, GameLevel, GuessOutcome } from '../models/mastermind.models';
import { joinUrl, normalizeBoardState, normalizeGuessOutcome } from '../utils/mastermind.utils';
import { ApiSettingsService } from './api-settings.service';

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly http = inject(HttpClient);
  private readonly apiSettingsService = inject(ApiSettingsService);

  getCurrentGame() {
    return this.http
      .get(this.buildUrl('game/status'), {
        observe: 'response'
      })
      .pipe(
        map((response: HttpResponse<unknown>) => {
          if (response.status === 204 || response.body === null) {
            return null;
          }

          return normalizeBoardState(response.body) as GameBoardState;
        })
      );
  }

  createGame(level: GameLevel) {
    return this.http
      .post(this.buildUrl('game/create'), { level })
      .pipe(map((response) => normalizeBoardState(response)));
  }

  submitGuess(guess: number[]) {
    return this.http
      .post(this.buildUrl('game/guess'), { guess })
      .pipe(map((response) => normalizeGuessOutcome(response) as GuessOutcome));
  }

  giveUp() {
    return this.http
      .post(this.buildUrl('game/give-up'), {})
      .pipe(map((response) => normalizeBoardState(response)));
  }

  private buildUrl(path: string): string {
    return joinUrl(this.apiSettingsService.baseUrl(), path);
  }
}