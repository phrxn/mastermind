import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { formatApiError } from '../../core/utils/mastermind.utils';
import { ApiSettingsPanelComponent } from '../../shared/components/api-settings-panel.component';

@Component({
  selector: 'app-login-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, ApiSettingsPanelComponent],
  template: `
    <section class="auth-layout auth-layout-login">
      <div class="hero-copy">
        <p class="eyebrow login-main-title">Mastermind online</p>
        <h1>Entre e se torne o rei dos códigos!</h1>
      </div>

      <div class="auth-column">
        <form class="card-surface auth-card" [formGroup]="form" (ngSubmit)="submit()">
          <div class="section-heading">
            <div>
              <p class="eyebrow">Acesso</p>
              <h2>Entrar</h2>
            </div>
          </div>

          <label class="form-field">
            <span>Usuário</span>
            <input type="text" formControlName="username" autocomplete="username" placeholder="Seu usuário" />
          </label>

          <label class="form-field">
            <span>Senha</span>
            <input type="password" formControlName="password" autocomplete="current-password" placeholder="Sua senha" />
          </label>

          @if (error()) {
            <p class="feedback error">{{ error() }}</p>
          }

          <div class="inline-actions">
            <button type="submit" class="primary-button" [disabled]="loading() || form.invalid">
              {{ loading() ? 'Entrando...' : 'Entrar' }}
            </button>
            <a routerLink="/auth/signup" class="text-link">Criar conta</a>
          </div>
        </form>

        <app-api-settings-panel title="Endereço da API" />
      </div>
    </section>
  `
})
export class LoginPageComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal('');
  readonly form = new FormGroup({
    username: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required] })
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.error.set('');
    this.loading.set(true);

    this.authService
      .login(this.form.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigate(['/app/play']);
        },
        error: (error) => {
          this.error.set(formatApiError(error, 'Não foi possível autenticar com a API.'));
        }
      });
  }
}