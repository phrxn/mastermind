import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Profile } from '../models/mastermind.models';
import { ProfileService } from './profile.service';

const BASE = 'http://localhost:8080/api/v1';

const PROFILE_PAYLOAD: Profile = {
  name: 'Player One',
  nickname: 'player1',
  email: 'player@example.com',
  age: 24
};

describe('ProfileService', () => {
  let service: ProfileService;
  let httpController: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(ProfileService);
    httpController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpController.verify();
  });

  describe('getProfile()', () => {
    it('should call GET users/profile and return the profile', () => {
      let result: unknown;
      service.getProfile().subscribe((value) => {
        result = value;
      });

      const req = httpController.expectOne(`${BASE}/users/profile`);
      expect(req.request.method).toBe('GET');
      req.flush(PROFILE_PAYLOAD);

      expect(result).toEqual(PROFILE_PAYLOAD);
    });
  });

  describe('updateProfile()', () => {
    it('should call PUT users/profile with the given payload', () => {
      const update = { name: 'Updated Name', nickname: 'updated1', age: 25 };
      service.updateProfile(update).subscribe();

      const req = httpController.expectOne(`${BASE}/users/profile`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual(update);
      req.flush({ ...PROFILE_PAYLOAD, ...update });
    });

    it('should return the updated profile returned by the API', () => {
      let result: unknown;
      const update = { name: 'New Name', nickname: 'new1', age: 30 };
      service.updateProfile(update).subscribe((value) => {
        result = value;
      });

      const updated = { ...PROFILE_PAYLOAD, ...update };
      httpController.expectOne(`${BASE}/users/profile`).flush(updated);

      expect((result as Profile).name).toBe('New Name');
      expect((result as Profile).nickname).toBe('new1');
    });
  });

  describe('changePassword()', () => {
    it('should call POST users/password with the given payload', () => {
      service.changePassword({ currentPassword: 'old-pass', newPassword: 'new-pass' }).subscribe();

      const req = httpController.expectOne(`${BASE}/users/password`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual({ currentPassword: 'old-pass', newPassword: 'new-pass' });
      req.flush({});
    });

    it('should map the response to void', () => {
      let result: unknown = 'untouched';
      service.changePassword({ currentPassword: 'old-pass', newPassword: 'new-pass' }).subscribe((value) => {
        result = value;
      });

      httpController.expectOne(`${BASE}/users/password`).flush({});

      expect(result).toBeUndefined();
    });
  });
});
