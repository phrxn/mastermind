import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AuthService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  it('should login and store the authenticated session', () => {
    let completed = false;

    service.login({ username: 'player', password: '123@Abc' }).subscribe(() => {
      completed = true;
    });

    const request = httpController.expectOne('http://localhost:8080/auth/login');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ username: 'player', password: '123@Abc' });
    request.flush({ token: 'token-1', tokenType: 'Bearer' });

    expect(completed).toBeTrue();
    expect(service.isAuthenticated()).toBeTrue();
    expect(service.token()).toBe('token-1');
  });

  it('should refresh the session using stored credentials', () => {
    service.login({ username: 'player', password: '123@Abc' }).subscribe();
    httpController.expectOne('http://localhost:8080/auth/login').flush({ token: 'token-1', tokenType: 'Bearer' });

    let refreshedToken = '';
    service.refreshSession().subscribe((token) => {
      refreshedToken = token;
    });

    const request = httpController.expectOne('http://localhost:8080/auth/login');
    expect(request.request.body).toEqual({ username: 'player', password: '123@Abc' });
    request.flush({ token: 'token-2', tokenType: 'Bearer' });

    expect(refreshedToken).toBe('token-2');
    expect(service.token()).toBe('token-2');
  });

  it('should clear the session when refresh fails', () => {
    service.login({ username: 'player', password: '123@Abc' }).subscribe();
    httpController.expectOne('http://localhost:8080/auth/login').flush({ token: 'token-1', tokenType: 'Bearer' });

    service.refreshSession().subscribe({
      error: () => undefined
    });

    const request = httpController.expectOne('http://localhost:8080/auth/login');
    request.flush({ status: 401, error: 'Token inválido' }, { status: 401, statusText: 'Unauthorized' });

    expect(service.isAuthenticated()).toBeFalse();
    expect(service.token()).toBeNull();
  });
});