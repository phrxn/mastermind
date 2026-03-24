import { Injectable, signal } from '@angular/core';

const STORAGE_KEY = 'mastermind.api.baseUrl';
const DEFAULT_API_URL = 'http://localhost:8080/api/v1';

@Injectable({ providedIn: 'root' })
export class ApiSettingsService {
  readonly baseUrl = signal(localStorage.getItem(STORAGE_KEY) ?? DEFAULT_API_URL);

  updateBaseUrl(value: string): void {
    const sanitized = value.trim().replace(/\/+$/, '') || DEFAULT_API_URL;
    localStorage.setItem(STORAGE_KEY, sanitized);
    this.baseUrl.set(sanitized);
  }
}