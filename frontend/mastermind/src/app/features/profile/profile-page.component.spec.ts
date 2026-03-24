import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService } from '../../core/services/profile.service';
import { ProfilePageComponent } from './profile-page.component';

describe('ProfilePageComponent', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let profileService: jasmine.SpyObj<ProfileService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['updateUsername']);
    profileService = jasmine.createSpyObj<ProfileService>('ProfileService', ['getProfile', 'updateProfile', 'changePassword']);
    profileService.getProfile.and.returnValue(of({ age: 21, email: 'player@example.com', name: 'Player', nickname: 'player1' }));
    profileService.updateProfile.and.returnValue(of({ age: 25, email: 'player@example.com', name: 'Updated', nickname: 'updated' }));
    profileService.changePassword.and.returnValue(of(void 0));

    await TestBed.configureTestingModule({
      imports: [ProfilePageComponent],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ProfileService, useValue: profileService }
      ]
    }).compileComponents();
  });

  it('should load and save the player profile', () => {
    const fixture = TestBed.createComponent(ProfilePageComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    component.profileForm.setValue({ age: 25, email: 'player@example.com', name: 'Updated', nickname: 'updated' });
    component.saveProfile();

    expect(profileService.updateProfile).toHaveBeenCalledWith({ age: 25, name: 'Updated', nickname: 'updated' });
    expect(authService.updateUsername).toHaveBeenCalledWith('updated');
    expect(component.profileFeedback()).toContain('Perfil atualizado');
  });

  it('should block password change when confirmation does not match', () => {
    const fixture = TestBed.createComponent(ProfilePageComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    component.passwordForm.setValue({ currentPassword: '123456', newPassword: 'abcdef', confirmPassword: 'abcdeg' });
    component.changePassword();

    expect(profileService.changePassword).not.toHaveBeenCalled();
    expect(component.passwordFeedback()).toContain('confirmação da senha');
  });
});