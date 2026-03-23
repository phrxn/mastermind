import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { formatApiError } from '../../core/utils/mastermind.utils';

@Component({
  selector: 'app-signup-page',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <section class="auth-layout auth-layout-signup">
      <div class="hero-copy">
        <p class="eyebrow">Novo jogador</p>
        <h1>Crie a conta e prepare a primeira combinacao.</h1>
        <p>
          Cadastro simples com os campos pedidos pela API e o mesmo visual do jogo para manter a navegacao coesa.
        </p>
      </div>

      <form class="card-surface auth-card" [formGroup]="form" (ngSubmit)="submit()">
        <div class="section-heading">
          <div>
            <p class="eyebrow">Cadastro</p>
            <h2>Signup</h2>
          </div>
        </div>

        <label class="form-field">
          <span>Nome</span>
          <input type="text" formControlName="name" autocomplete="name" />
        </label>

        <label class="form-field">
          <span>Email</span>
          <input type="email" formControlName="email" autocomplete="email" />
        </label>

        <div class="form-grid two-columns">
          <label class="form-field">
            <span>Nickname</span>
            <input type="text" formControlName="nickname" autocomplete="nickname" />
          </label>

          <label class="form-field">
            <span>Idade</span>
            <input type="number" formControlName="age" min="1" />
          </label>
        </div>

        <label class="form-field">
          <span>Senha</span>
          <input type="password" formControlName="password" autocomplete="new-password" />
        </label>

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
    </section>
  `
})
export class SignupPageComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly error = signal('');
  readonly success = signal('');
  readonly form = new FormGroup({
    name: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    email: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.email] }),
    nickname: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    age: new FormControl(18, { nonNullable: true, validators: [Validators.required, Validators.min(1)] }),
    password: new FormControl('', { nonNullable: true, validators: [Validators.required, Validators.minLength(6)] })
  });

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.error.set('');
    this.success.set('');
    this.loading.set(true);

    this.authService
      .signup(this.form.getRawValue())
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: () => {
          this.success.set('Conta criada. Voce ja pode fazer login.');
          setTimeout(() => {
            void this.router.navigate(['/auth/login']);
          }, 900);
        },
        error: (error) => {
          this.error.set(formatApiError(error, 'Nao foi possivel criar a conta.'));
        }
      });
  }
}