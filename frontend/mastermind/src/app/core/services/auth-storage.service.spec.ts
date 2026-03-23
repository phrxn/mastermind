import { TestBed } from '@angular/core/testing';
import { AuthStorageService } from './auth-storage.service';

describe('AuthStorageService', () => {
  let service: AuthStorageService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthStorageService);
  });

  it('should persist and recover a session', () => {
    const session = {
      token: 'jwt-token',
      tokenType: 'Bearer',
      username: 'player',
      password: '123@Abc'
    };

    service.write(session);

    expect(service.read()).toEqual(session);
  });

  it('should clear an invalid stored payload', () => {
    localStorage.setItem('mastermind.auth.session', '{invalid json');

    expect(service.read()).toBeNull();
    expect(localStorage.getItem('mastermind.auth.session')).toBeNull();
  });
});