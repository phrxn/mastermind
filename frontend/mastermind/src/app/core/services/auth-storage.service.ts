import { Injectable } from '@angular/core';
import { AuthSession } from '../models/mastermind.models';

const STORAGE_KEY = 'mastermind.auth.session';

@Injectable({ providedIn: 'root' })
export class AuthStorageService {
  read(): AuthSession | null {
    const rawValue = localStorage.getItem(STORAGE_KEY);

    if (!rawValue) {
      return null;
    }

    try {
      return JSON.parse(rawValue) as AuthSession;
    } catch {
      localStorage.removeItem(STORAGE_KEY);
      return null;
    }
  }

  write(session: AuthSession): void {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
  }

  clear(): void {
    localStorage.removeItem(STORAGE_KEY);
  }
}