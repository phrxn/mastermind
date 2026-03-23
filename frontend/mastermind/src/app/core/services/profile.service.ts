import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { map } from 'rxjs';
import { PasswordChangeRequest, Profile } from '../models/mastermind.models';
import { joinUrl } from '../utils/mastermind.utils';
import { ApiSettingsService } from './api-settings.service';

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);
  private readonly apiSettingsService = inject(ApiSettingsService);

  getProfile() {
    return this.http.get<Profile>(this.buildUrl('users/profile'));
  }

  updateProfile(payload: Omit<Profile, 'email'>) {
    return this.http.put<Profile>(this.buildUrl('users/profile'), payload);
  }

  changePassword(payload: PasswordChangeRequest) {
    return this.http.post(this.buildUrl('users/password'), payload).pipe(map(() => void 0));
  }

  private buildUrl(path: string): string {
    return joinUrl(this.apiSettingsService.baseUrl(), path);
  }
}