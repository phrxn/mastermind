import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import {
  RankingItem,
  RankingResponse,
} from '../../core/models/mastermind.models';
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
        <section class="card-surface loading-card">
          Carregando ranking...
        </section>
      } @else if (error()) {
        <section class="card-surface feedback error">{{ error() }}</section>
      } @else {
        <section class="tabs-row card-surface">
          @for (tab of tabs; track tab.key) {
            <button
              type="button"
              class="tab-button"
              [class.active-tab]="activeTab() === tab.key"
              (click)="activeTab.set(tab.key)"
            >
              {{ tab.label }}
            </button>
          }
        </section>

        @if (currentItems().length === 0) {
          <section class="card-surface empty-state">
            Ainda não existem partidas ranqueadas nessa faixa.
          </section>
        } @else {
          <div class="ranking-list" aria-label="Ranking atual">
            @for (
              item of currentItems();
              track item.gameUuidPublic;
              let index = $index
            ) {
              <button
                type="button"
                class="ranking-card card-surface"
                [class.top-rank]="index < 3"
                [class.top-rank-1]="index === 0"
                [class.top-rank-2]="index === 1"
                [class.top-rank-3]="index === 2"
                (click)="openDetail(item.gameUuidPublic)"
              >
                <div class="ranking-position" aria-hidden="true">
                  #{{ index + 1 }}
                </div>
                <div class="history-card-top">
                  <div>
                    <strong>{{ item.userNickname }}</strong>
                    <p class="ranking-subtitle">
                      <span *ngIf="index < 3" class="medal-icon">
                        {{ medalIcons[index] }}
                      </span>
                      Reprodução disponível para consulta
                    </p>
                  </div>
                  <span class="level-pill">{{
                    levelLabels[item.gameLevel]
                  }}</span>
                </div>
                <dl class="card-data-grid">
                  <div>
                    <dt>Pontos</dt>
                    <dd>{{ item.pointsMaked }}</dd>
                  </div>
                  <div>
                    <dt>Tentativas</dt>
                    <dd>{{ item.attemptsUsed }}</dd>
                  </div>
                  <div>
                    <dt>Criado em</dt>
                    <dd>{{ formatDate(item.createdAt) }}</dd>
                  </div>
                  <div>
                    <dt>Finalizado</dt>
                    <dd>{{ formatDate(item.finishedAt) }}</dd>
                  </div>
                </dl>
              </button>
            }
          </div>
        }
      }
    </section>
  `,
})
export class RankingPageComponent {
  medalIcons: Record<number, string> = {
    0: '🥇', // ouro
    1: '🥈', // prata
    2: '🥉', // bronze
  };
  private readonly rankingService = inject(RankingService);
  private readonly router = inject(Router);

  readonly tabs = [
    { key: 'easy' as const, label: 'Fácil' },
    { key: 'normal' as const, label: 'Normal' },
    { key: 'hard' as const, label: 'Difícil' },
    { key: 'mastermind' as const, label: 'Mastermind' },
  ];
  readonly activeTab = signal<RankingTab>('easy');
  readonly loading = signal(true);
  readonly error = signal('');
  readonly ranking = signal<RankingResponse | null>(null);
  readonly levelLabels = levelLabels;
  readonly currentItems = computed(() =>
    this.getItemsForTab(this.activeTab(), this.ranking()),
  );

  constructor() {
    this.rankingService
      .getRanking()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (ranking) => this.ranking.set(ranking),
        error: (error) =>
          this.error.set(
            formatApiError(error, 'Não foi possível carregar o ranking.'),
          ),
      });
  }

  openDetail(uuid: string): void {
    void this.router.navigate(['/app/ranking', uuid]);
  }

  formatDate(value: string | null | undefined): string {
    return value
      ? new Intl.DateTimeFormat('pt-BR', {
          dateStyle: 'short',
          timeStyle: 'short',
        }).format(new Date(value))
      : '-';
  }

  private getItemsForTab(
    tab: RankingTab,
    ranking: RankingResponse | null,
  ): RankingItem[] {
    if (!ranking) {
      return [];
    }

    switch (tab) {
      case 'easy':
        return ranking.top10EasyGames;
      case 'normal':
        return ranking.top10NormalGames;
      case 'hard':
        return ranking.top10HardGames;
      case 'mastermind':
        return ranking.top10MastermindGames;
    }
  }
}
