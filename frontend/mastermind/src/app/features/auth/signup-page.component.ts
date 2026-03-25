import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { AbstractControl, FormControl, FormGroup, ReactiveFormsModule, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { formatApiError } from '../../core/utils/mastermind.utils';

const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;

  if (!password || !confirmPassword) {
    return null;
  }

  return password === confirmPassword ? null : { passwordMismatch: true };
};

@Component({
  selector: 'app-signup-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="auth-layout auth-layout-signup">
      <div class="hero-copy">
        <p class="eyebrow login-main-title">Novo jogador</p>
        <h1>Crie a sua conta e prepare a primeira combinação.</h1>
      </div>

      <form class="card-surface auth-card" [formGroup]="form" (ngSubmit)="openConfirmDialog()">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Cadastro</p>
            <h2>Criar conta</h2>
          </div>
        </div>

        <label class="form-field">
          <span>Nome</span>
          <input type="text" formControlName="name" autocomplete="name" placeholder="Seu nome" />
        </label>

        <label class="form-field">
          <span>Email</span>
          <input type="email" formControlName="email" autocomplete="email" placeholder="Exemplo: usuario@exemplo.com" />
        </label>

        <label class="form-field">
          <span>Nickname</span>
          <input type="text" formControlName="nickname" autocomplete="nickname" placeholder="Seu apelido" />
        </label>

        <label class="form-field">
          <span>Idade</span>
          <input type="number" formControlName="age" min="1" />
        </label>

        <label class="form-field">
          <span>Senha</span>
          <input type="password" formControlName="password" autocomplete="new-password" />
        </label>

        <label class="form-field">
          <span>Confirmar senha</span>
          <input type="password" formControlName="confirmPassword" autocomplete="new-password" />
        </label>

        @if (form.hasError('passwordMismatch') && form.touched) {
          <p class="feedback error">A confirmação da senha precisa ser igual à senha informada.</p>
        }

        @if (error()) {
          <p class="feedback error">{{ error() }}</p>
        }

        @if (success()) {
          <p class="feedback success">{{ success() }}</p>
        }

        <div class="inline-actions">
          <button type="submit" class="primary-button" [disabled]="loading() || form.invalid">
            {{ loading() ? 'Criando...' : 'Criar conta' }}
          </button>
          <a routerLink="/auth/login" class="text-link">Voltar ao login</a>
        </div>
      </form>

      @if (confirmDialogOpen()) {
        <div class="dialog-backdrop" (click)="closeConfirmDialog()">
          <section class="dialog-panel card-surface" role="dialog" aria-modal="true" aria-label="Confirmar cadastro" (click)="$event.stopPropagation()">
            <div class="dialog-copy">
              <p class="eyebrow">Confirmação</p>
              <h3>Confirmar cadastro</h3>
              <p class="dialog-message">Revise os dados antes de enviar. Deseja criar este usuário agora?</p>
            </div>

            <div class="dialog-actions">
              <button type="button" class="secondary-button" (click)="closeConfirmDialog()">Voltar</button>
              <button type="button" class="primary-button" (click)="submitConfirmed()">Confirmar envio</button>
            </div>
          </section>
        </div>
      }
    </section>
  `
})
export class SignupPageComponent {
  private readonly authService = inject(AuthService);

  readonly loading = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly confirmDialogOpen = signal(false);
  readonly form = new FormGroup(
    {
      name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
      email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
      nickname: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
      age: new FormControl(18, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
      password: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(1)] }),
      confirmPassword: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(1)] })
    },
    { validators: [passwordMatchValidator] }
  );

  openConfirmDialog(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.confirmDialogOpen.set(true);
  }

  closeConfirmDialog(): void {
    this.confirmDialogOpen.set(false);
  }

  submitConfirmed(): void {
    this.confirmDialogOpen.set(false);

    this.error.set('');
    this.success.set('');
    this.loading.set(true);

    this.authService
      .signup(this.buildSignupPayload())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.form.reset({
            name: '',
            email: '',
            nickname: '',
            age: 18,
            password: '',
            confirmPassword: ''
          });
          this.success.set('Conta criada com sucesso. Agora você pode entrar quando quiser.');
        },
        error: (error) => {
          this.error.set(formatApiError(error, 'Não foi possível criar a conta.'));
        }
      });
  }

  private buildSignupPayload() {
    const { confirmPassword: _, ...payload } = this.form.getRawValue();
    return payload;
  }
}