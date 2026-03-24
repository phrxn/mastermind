import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-shell">
      <header class="mobile-topbar card-surface">
        <button type="button" class="menu-toggle" (click)="toggleMenu()">Menu</button>
        <div>
          <p class="eyebrow">Mastermind</p>
          <strong>{{ authService.username() || 'Jogador' }}</strong>
        </div>
      </header>

      <div class="shell-layout">
        <aside class="side-nav card-surface" [class.open]="menuOpen()">
          <div class="brand-block">
            <p class="eyebrow">Puzzle arcade</p>
            <h1>Mastermind</h1>
            <p>Leia o padrão, administre as tentativas e se torne o "Mastermind!".</p>
          </div>

          <nav class="nav-links">
            @for (item of navItems; track item.path) {
              <a [routerLink]="item.path" routerLinkActive="active-link" (click)="menuOpen.set(false)">
                {{ item.label }}
              </a>
            }
            <button type="button" class="logout-button" (click)="logout()">Sair</button>
          </nav>
        </aside>

        <main class="shell-content">
          <router-outlet />
        </main>
      </div>
    </div>
  `
})
export class AppShellComponent {
  readonly authService = inject(AuthService);
  readonly menuOpen = signal(false);
  readonly navItems = [
    { path: '/app/play', label: 'Jogar' },
    { path: '/app/rules', label: 'Regras' },
    { path: '/app/history', label: 'Histórico' },
    { path: '/app/ranking', label: 'Ranking' },
    { path: '/app/profile', label: 'Perfil' }
  ];

  logout(): void {
    this.authService.logout(true);
  }

  toggleMenu(): void {
    this.menuOpen.update((value) => !value);
  }
}