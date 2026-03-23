import { HttpBackend, HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, finalize, map, of, shareReplay, tap, throwError } from 'rxjs';
import { AuthSession, LoginRequest, LoginResponse, SignupRequest } from '../models/mastermind.models';
import { joinUrl } from '../utils/mastermind.utils';
import { ApiSettingsService } from './api-settings.service';
import { AuthStorageService } from './auth-storage.service';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly router = inject(Router);
  private readonly apiSettingsService = inject(ApiSettingsService);
  private readonly authStorageService = inject(AuthStorageService);
  private readonly rawHttp = new HttpClient(inject(HttpBackend));
  private readonly sessionState = signal<AuthSession | null>(this.authStorageService.read());
  private refreshRequest$: Observable<string> | null = null;

  readonly token = computed(() => this.sessionState()?.token ?? null);
  readonly tokenType = computed(() => this.sessionState()?.tokenType ?? 'Bearer');
  readonly isAuthenticated = computed(() => Boolean(this.sessionState()?.token));
  readonly username = computed(() => this.sessionState()?.username ?? '');

  login(credentials: LoginRequest): Observable<void> {
    return this.rawHttp
      .post<LoginResponse>(this.buildUrl('auth/login'), credentials)
      .pipe(
        tap((response) => {
          this.persistSession({
            token: response.token,
            tokenType: response.tokenType || 'Bearer',
            username: credentials.username,
            password: credentials.password
          });
        }),
        map(() => void 0)
      );
  }

  signup(payload: SignupRequest): Observable<void> {
    return this.rawHttp.post(this.buildUrl('auth/register'), payload).pipe(map(() => void 0));
  }

  refreshSession(): Observable<string> {
    const session = this.sessionState();

    if (!session?.username || !session.password) {
      return throwError(() => new Error('Sem credenciais salvas para renovar a sessao.'));
    }

    if (this.refreshRequest$) {
      return this.refreshRequest$;
    }

    this.refreshRequest$ = this.rawHttp
      .post<LoginResponse>(this.buildUrl('auth/login'), {
        username: session.username,
        password: session.password
      })
      .pipe(
        map((response) => ({
          token: response.token,
          tokenType: response.tokenType || session.tokenType,
          username: session.username,
          password: session.password
        })),
        tap((newSession) => this.persistSession(newSession)),
        map((newSession) => newSession.token),
        catchError((error) => {
          this.logout(true);
          return throwError(() => error);
        }),
        finalize(() => {
          this.refreshRequest$ = null;
        }),
        shareReplay(1)
      );

    return this.refreshRequest$;
  }

  logout(redirectToLogin = false): void {
    this.authStorageService.clear();
    this.sessionState.set(null);

    if (redirectToLogin) {
      void this.router.navigate(['/auth/login']);
    }
  }

  restoreSession(): Observable<boolean> {
    return of(this.isAuthenticated());
  }

  private persistSession(session: AuthSession): void {
    this.authStorageService.write(session);
    this.sessionState.set(session);
  }

  private buildUrl(path: string): string {
    return joinUrl(this.apiSettingsService.baseUrl(), path);
  }
}