import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { finalize } from 'rxjs';
import { GameLevel, GameStatus, HistoryResponse, HistorySummaryItem } from '../../core/models/mastermind.models';
import { HistoryService } from '../../core/services/history.service';
import { formatApiError, levelLabels, levelOptions, statusLabels } from '../../core/utils/mastermind.utils';

@Component({
  selector: 'app-history-page',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="page-stack">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Histórico</p>
          <h2>Suas partidas</h2>
        </div>
      </div>

      @if (loading()) {
        <section class="card-surface loading-card">Buscando histórico...</section>
      } @else if (error()) {
        <section class="card-surface feedback error">{{ error() }}</section>
      } @else {
        <section class="card-surface">
          <div class="section-heading compact">
            <div>
              <p class="eyebrow">Melhores pontuações</p>
              <h3>Resumo por nível</h3>
            </div>
          </div>

          <div class="table-shell">
            <table class="score-table">
              <thead>
                <tr>
                  <th>Nível</th>
                  <th>Pontos</th>
                  <th>Tentativas</th>
                  <th>Jogando em</th>
                  <th>Finalizado em</th>
                </tr>
              </thead>
              <tbody>
                @for (option of levelOptions; track option.level) {
                  <tr>
                    <td>{{ option.label }}</td>
                    <td>{{ bestScore(option.level)?.pointsMaked ?? '-' }}</td>
                    <td>{{ bestScore(option.level)?.attemptsUsed ?? '-' }}</td>
                    <td>{{ formatDate(bestScore(option.level)?.createdAt) }}</td>
                    <td>{{ formatDate(bestScore(option.level)?.finishedAt) }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </section>

        <section class="page-stack">
          <div class="section-heading compact">
            <div>
              <p class="eyebrow">Histórico completo</p>
              <h3>{{ history()?.gameHistoryFull?.length ?? 0 }} partidas registradas</h3>
            </div>
          </div>

          @if (pageItems().length === 0) {
            <section class="card-surface empty-state">Nenhuma partida encontrada.</section>
          } @else {
            <div class="cards-grid">
              @for (item of pageItems(); track item.publicUuid) {
                <button type="button" class="history-card card-surface" (click)="openDetail(item.publicUuid)">
                  <div class="history-card-top">
                    <strong>{{ levelLabels[item.level] }}</strong>
                    <span class="status-pill" [ngClass]="statusClasses[item.status]">{{ statusLabels[item.status] }}</span>
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

            <div class="pagination-bar card-surface">
              <button type="button" class="secondary-button" (click)="previousPage()" [disabled]="pageIndex() === 0">Voltar</button>
              <span>Página {{ pageIndex() + 1 }} de {{ totalPages() }}</span>
              <button type="button" class="secondary-button" (click)="nextPage()" [disabled]="pageIndex() + 1 >= totalPages()">Próxima</button>
            </div>
          }
        </section>
      }
    </section>
  `
})
export class HistoryPageComponent {

  statusClasses: Record<GameStatus, string> = {
    'IN_PROGRESS': 'status-pill-in-progress',
    'WON': 'status-pill-won',
    'LOST': 'status-pill-lost',
    'GAVE_UP': 'status-pill-gave-up'
  };

  private readonly historyService = inject(HistoryService);
  private readonly router = inject(Router);

  readonly loading = signal(true);
  readonly error = signal('');
  readonly history = signal<HistoryResponse | null>(null);
  readonly pageIndex = signal(0);
  readonly pageSize = 12;
  readonly levelOptions = levelOptions;
  readonly levelLabels = levelLabels;
  readonly statusLabels = statusLabels;
  readonly bestScoreMap = computed(() => {
    const items = this.history()?.gameHistoryBestGames ?? [];
    return items.reduce<Partial<Record<GameLevel, HistorySummaryItem>>>((accumulator, item) => {
      accumulator[item.level] = item;
      return accumulator;
    }, {});
  });
  readonly totalPages = computed(() => Math.max(Math.ceil((this.history()?.gameHistoryFull.length ?? 0) / this.pageSize), 1));
  readonly pageItems = computed(() => {
    const start = this.pageIndex() * this.pageSize;
    return (this.history()?.gameHistoryFull ?? []).slice(start, start + this.pageSize);
  });

  constructor() {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.error.set('');

    this.historyService
      .getHistory()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (history) => this.history.set(history),
        error: (error) => this.error.set(formatApiError(error, 'Não foi possível carregar o histórico.'))
      });
  }

  openDetail(uuid: string): void {
    void this.router.navigate(['/app/history', uuid]);
  }

  bestScore(level: GameLevel): HistorySummaryItem | null {
    return this.bestScoreMap()[level] ?? null;
  }

  previousPage(): void {
    this.pageIndex.update((index) => Math.max(index - 1, 0));
  }

  nextPage(): void {
    this.pageIndex.update((index) => Math.min(index + 1, this.totalPages() - 1));
  }

  formatDate(value: string | null | undefined): string {
    return value ? new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(new Date(value)) : '-';
  }
}