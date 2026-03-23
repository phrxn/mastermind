import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

const RETRY_HEADER = 'x-auth-retried';

export const authInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const shouldSkipRefresh =
    request.url.includes('/auth/login') ||
    request.url.includes('/auth/register') ||
    request.headers.has(RETRY_HEADER);

  const token = authService.token();
  const tokenType = authService.tokenType();
  const requestWithAuth = token
    ? request.clone({
        setHeaders: {
          Authorization: `${tokenType} ${token}`
        }
      })
    : request;

  return next(requestWithAuth).pipe(
    catchError((error: unknown) => {
      if (shouldSkipRefresh || !isAuthenticationError(error)) {
        return throwError(() => error);
      }

      return authService.refreshSession().pipe(
        switchMap((freshToken) =>
          next(
            request.clone({
              headers: request.headers.set(RETRY_HEADER, 'true'),
              setHeaders: {
                Authorization: `${authService.tokenType()} ${freshToken}`
              }
            })
          )
        ),
        catchError((refreshError) => throwError(() => refreshError))
      );
    })
  );
};

function isAuthenticationError(error: unknown): boolean {
  if (!(error instanceof HttpErrorResponse)) {
    return false;
  }

  const apiError = error.error as { status?: number; error?: string } | null;
  return error.status === 401 || apiError?.status === 401 || apiError?.error === 'Token inválido';
}