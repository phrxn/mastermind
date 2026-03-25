import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService } from '../../core/services/profile.service';
import { formatApiError } from '../../core/utils/mastermind.utils';
import { ApiSettingsPanelComponent } from '../../shared/components/api-settings-panel.component';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ApiSettingsPanelComponent],
  template: `
    <section class="page-stack">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Perfil</p>
          <h2>Dados do jogador</h2>
        </div>
      </div>

      @if (loading()) {
        <section class="card-surface loading-card">Carregando perfil...</section>
      } @else {
        <div class="page-grid">
          <form class="card-surface" [formGroup]="profileForm" (ngSubmit)="saveProfile()">
            <div class="section-heading compact">
              <div>
                <p class="eyebrow">Identidade</p>
                <h3>Atualize seus dados</h3>
              </div>
            </div>

            <div class="form-grid two-columns">
              <label class="form-field">
                <span>Nome</span>
                <input type="text" formControlName="name" />
              </label>

              <label class="form-field">
                <span>Nickname</span>
                <input type="text" formControlName="nickname" />
              </label>
            </div>

            <div class="form-grid two-columns">
              <label class="form-field">
                <span>Idade</span>
                <input type="number" formControlName="age" min="1" />
              </label>

              <label class="form-field disabled-field">
                <span>Email</span>
                <input type="email" formControlName="email" />
              </label>
            </div>

            @if (profileFeedback()) {
              <p class="feedback" [class.success]="!profileError()" [class.error]="profileError()">{{ profileFeedback() }}</p>
            }

            <button type="submit" class="primary-button" [disabled]="savingProfile() || profileForm.invalid">
              {{ savingProfile() ? 'Salvando...' : 'Salvar alterações' }}
            </button>
          </form>

          <div class="page-stack">
            <form class="card-surface" [formGroup]="passwordForm" (ngSubmit)="changePassword()">
              <div class="section-heading compact">
                <div>
                  <p class="eyebrow">Segurança</p>
                  <h3>Alterar senha</h3>
                </div>
              </div>

              <label class="form-field">
                <span>Senha atual</span>
                <input type="password" formControlName="currentPassword" autocomplete="current-password" />
              </label>

              <label class="form-field">
                <span>Nova senha</span>
                <input type="password" formControlName="newPassword" autocomplete="new-password" />
              </label>

              <label class="form-field">
                <span>Confirmar nova senha</span>
                <input type="password" formControlName="confirmPassword" autocomplete="new-password" />
              </label>

              @if (passwordFeedback()) {
                <p class="feedback" [class.success]="!passwordError()" [class.error]="passwordError()">{{ passwordFeedback() }}</p>
              }

              <button type="submit" class="secondary-button" [disabled]="savingPassword() || passwordForm.invalid">
                {{ savingPassword() ? 'Enviando...' : 'Alterar senha' }}
              </button>
            </form>

            <app-api-settings-panel title="API configurada" />
          </div>
        </div>
      }
    </section>
  `
})
export class ProfilePageComponent {
  private readonly authService = inject(AuthService);
  private readonly profileService = inject(ProfileService);

  readonly loading = signal(true);
  readonly savingProfile = signal(false);
  readonly savingPassword = signal(false);
  readonly profileFeedback = signal('');
  readonly passwordFeedback = signal('');
  readonly profileError = signal(false);
  readonly passwordError = signal(false);
  readonly profileForm = new FormGroup({
    age: new FormControl(18, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
    email: new FormControl({ value: '', disabled: true }, { nonNullable: true }),
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    nickname: new FormControl('', { nonNullable: true, validators: [Validators.required] })
  });
  readonly passwordForm = new FormGroup({
    currentPassword: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    newPassword: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(0)] }),
    confirmPassword: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(0)] })
  });

  constructor() {
    this.profileService
      .getProfile()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (profile) => this.profileForm.reset(profile),
        error: () => {
          this.profileFeedback.set('Não foi possível carregar o perfil.');
          this.profileError.set(true);
        }
      });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    this.savingProfile.set(true);
    this.profileFeedback.set('');

    const { age, name, nickname } = this.profileForm.getRawValue();

    this.profileService
      .updateProfile({ age, name, nickname })
      .pipe(finalize(() => this.savingProfile.set(false)))
      .subscribe({
        next: (profile) => {
          this.profileForm.reset(profile);
          this.authService.updateUsername(nickname);
          this.profileError.set(false);
          this.profileFeedback.set('Perfil atualizado com sucesso.');
        },
        error: (error) => {
          this.profileError.set(true);
          this.profileFeedback.set(formatApiError(error, 'Não foi possível atualizar o perfil.'));
        }
      });
  }

  changePassword(): void {
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    const { currentPassword, newPassword, confirmPassword } = this.passwordForm.getRawValue();

    if (newPassword !== confirmPassword) {
      this.passwordError.set(true);
      this.passwordFeedback.set('A confirmação da senha precisa ser igual à nova senha.');
      return;
    }

    this.savingPassword.set(true);
    this.passwordFeedback.set('');

    this.profileService
      .changePassword({ currentPassword, newPassword })
      .pipe(finalize(() => this.savingPassword.set(false)))
      .subscribe({
        next: () => {
          this.passwordForm.reset({ currentPassword: '', newPassword: '', confirmPassword: '' });
          this.passwordError.set(false);
          this.passwordFeedback.set('Senha atualizada com sucesso.');
        },
        error: (error) => {
          this.passwordError.set(true);
          this.passwordFeedback.set(formatApiError(error, 'Não foi possível alterar a senha.'));
        }
      });
  }
}