import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { RankingItem, RankingResponse } from '../../core/models/mastermind.models';
import { RankingService } from '../../core/services/ranking.service';
import { formatApiError, levelLabels } from '../../core/utils/mastermind.utils';

type RankingTab = 'easy' | 'normal' | 'hard' | 'mastermind';

@Component({
  selector: 'app-ranking-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="page-stack">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Ranking</p>
          <h2>Top 10 por dificuldade</h2>
        </div>
      </div>

      @if (loading()) {
        <section class="card-surface loading-card">Carregando ranking...</section>
      } @else if (error()) {
        <section class="card-surface feedback error">{{ error() }}</section>
      } @else {
        <section class="tabs-row card-surface">
          @for (tab of tabs; track tab.key) {
            <button type="button" class="tab-button" [class.active-tab]="activeTab() === tab.key" (click)="activeTab.set(tab.key)">
              {{ tab.label }}
            </button>
          }
        </section>

        @if (currentItems().length === 0) {
          <section class="card-surface empty-state">Ainda nao existem partidas ranqueadas nessa faixa.</section>
        } @else {
          <div class="cards-grid ranking-grid">
            @for (item of currentItems(); track item.gameUuidPublic) {
              <button type="button" class="ranking-card card-surface" (click)="openDetail(item.gameUuidPublic)">
                <div class="history-card-top">
                  <strong>{{ item.userNickname }}</strong>
                  <span class="status-pill">{{ levelLabels[item.gameLevel] }}</span>
                </div>
                <dl class="card-data-grid">
                  <div><dt>Pontos</dt><dd>{{ item.pointsMaked }}</dd></div>
                  <div><dt>Tentativas</dt><dd>{{ item.attemptsUsed }}</dd></div>
                  <div><dt>Criado em</dt><dd>{{ formatDate(item.createdAt) }}</dd></div>
                  <div><dt>Finalizado</dt><dd>{{ formatDate(item.finishedAt) }}</dd></div>
                </dl>
              </button>
            }
          </div>
        }
      }
    </section>
  `
})
export class RankingPageComponent {
  private readonly rankingService = inject(RankingService);
  private readonly router = inject(Router);

  readonly tabs = [
    { key: 'easy' as const, label: 'Easy' },
    { key: 'normal' as const, label: 'Normal' },
    { key: 'hard' as const, label: 'Hard' },
    { key: 'mastermind' as const, label: 'Mastermind' }
  ];
  readonly activeTab = signal<RankingTab>('easy');
  readonly loading = signal(true);
  readonly error = signal('');
  readonly ranking = signal<RankingResponse | null>(null);
  readonly levelLabels = levelLabels;
  readonly currentItems = computed(() => this.getItemsForTab(this.activeTab(), this.ranking()));

  constructor() {
    this.rankingService
      .getRanking()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (ranking) => this.ranking.set(ranking),
        error: (error) => this.error.set(formatApiError(error, 'Nao foi possivel carregar o ranking.'))
      });
  }

  openDetail(uuid: string): void {
    void this.router.navigate(['/app/ranking', uuid]);
  }

  formatDate(value: string | null | undefined): string {
    return value ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value)) : '-';
  }

  private getItemsForTab(tab: RankingTab, ranking: RankingResponse | null): RankingItem[] {
    if (!ranking) {
      return [];
    }

    switch (tab) {
      case 'easy':
        return ranking.top10EasyGamesList;
      case 'normal':
        return ranking.top10NormalGamesList;
      case 'hard':
        return ranking.top10HardGamesList;
      case 'mastermind':
        return ranking.top10MastermindGamesList;
    }
  }
}