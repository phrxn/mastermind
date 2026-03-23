import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { joinUrl, normalizeBoardState, normalizeHistoryResponse } from '../utils/mastermind.utils';
import { ApiSettingsService } from './api-settings.service';

@Injectable({ providedIn: 'root' })
export class HistoryService {
  private readonly http = inject(HttpClient);
  private readonly apiSettingsService = inject(ApiSettingsService);

  getHistory() {
    return this.http
      .get(this.buildUrl('users/history'))
      .pipe(map((response) => normalizeHistoryResponse(response)));
  }

  getHistoryDetail(uuid: string) {
    return this.http
      .get(this.buildUrl(`users/history/${uuid}`))
      .pipe(map((response) => normalizeBoardState(response)));
  }

  private buildUrl(path: string): string {
    return joinUrl(this.apiSettingsService.baseUrl(), path);
  }
}