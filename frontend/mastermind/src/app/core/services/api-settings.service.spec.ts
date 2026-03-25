import { TestBed } from '@angular/core/testing';
import { ApiSettingsService } from './api-settings.service';

const STORAGE_KEY = 'mastermind.api.baseUrl';
const DEFAULT_URL = 'http://localhost:8080/api/v1';

describe('ApiSettingsService', () => {
  let service: ApiSettingsService;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({});
    service = TestBed.inject(ApiSettingsService);
  });

  it('should use the default URL when localStorage is empty', () => {
    expect(service.baseUrl()).toBe(DEFAULT_URL);
  });

  it('should load a saved URL from localStorage on startup', () => {
    localStorage.setItem(STORAGE_KEY, 'http://staging.example.com/api');
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({});
    const freshService = TestBed.inject(ApiSettingsService);

    expect(freshService.baseUrl()).toBe('http://staging.example.com/api');
  });

  it('should update the signal and persist the URL to localStorage', () => {
    service.updateBaseUrl('http://prod.example.com/api');

    expect(service.baseUrl()).toBe('http://prod.example.com/api');
    expect(localStorage.getItem(STORAGE_KEY)).toBe('http://prod.example.com/api');
  });

  it('should strip trailing slashes when saving', () => {
    service.updateBaseUrl('http://localhost:9090/api/v2///');

    expect(service.baseUrl()).toBe('http://localhost:9090/api/v2');
    expect(localStorage.getItem(STORAGE_KEY)).toBe('http://localhost:9090/api/v2');
  });

  it('should fall back to the default URL when an empty string is saved', () => {
    service.updateBaseUrl('   ');

    expect(service.baseUrl()).toBe(DEFAULT_URL);
    expect(localStorage.getItem(STORAGE_KEY)).toBe(DEFAULT_URL);
  });
});
