import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { joinUrl, normalizeBoardState, normalizeRankingResponse } from '../utils/mastermind.utils';
import { ApiSettingsService } from './api-settings.service';

@Injectable({ providedIn: 'root' })
export class RankingService {
  private readonly http = inject(HttpClient);
  private readonly apiSettingsService = inject(ApiSettingsService);

  getRanking() {
    return this.http
      .get(this.buildUrl('ranking'))
      .pipe(map((response) => normalizeRankingResponse(response)));
  }

  getRankingDetail(uuid: string) {
    return this.http
      .get(this.buildUrl(`ranking/${uuid}`))
      .pipe(map((response) => normalizeBoardState(response)));
  }

  private buildUrl(path: string): string {
    return joinUrl(this.apiSettingsService.baseUrl(), path);
  }
}