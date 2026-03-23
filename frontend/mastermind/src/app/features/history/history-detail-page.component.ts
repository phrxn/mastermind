import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { GameBoardState } from '../../core/models/mastermind.models';
import { HistoryService } from '../../core/services/history.service';
import { formatApiError } from '../../core/utils/mastermind.utils';
import { MastermindBoardComponent } from '../../shared/components/mastermind-board.component';

@Component({
  selector: 'app-history-detail-page',
  standalone: true,
  imports: [CommonModule, MastermindBoardComponent],
  template: `
    <section class="page-stack">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Historico detalhado</p>
          <h2>Replay da partida</h2>
        </div>
        <button type="button" class="secondary-button" (click)="goBack()">Voltar</button>
      </div>

      @if (loading()) {
        <section class="card-surface loading-card">Montando o tabuleiro...</section>
      } @else if (error()) {
        <section class="card-surface feedback error">{{ error() }}</section>
      } @else {
        <app-mastermind-board [board]="board()" [revealSecret]="true" />
      }
    </section>
  `
})
export class HistoryDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly historyService = inject(HistoryService);

  readonly loading = signal(true);
  readonly error = signal('');
  readonly board = signal<GameBoardState | null>(null);

  constructor() {
    const uuid = this.route.snapshot.paramMap.get('uuid') ?? '';
    this.historyService
      .getHistoryDetail(uuid)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (board) => this.board.set(board),
        error: (error) => this.error.set(formatApiError(error, 'Nao foi possivel abrir os detalhes da partida.'))
      });
  }

  goBack(): void {
    void this.router.navigate(['/app/history']);
  }
}