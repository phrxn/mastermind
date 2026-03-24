import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs';
import { GameBoardState } from '../../core/models/mastermind.models';
import { RankingService } from '../../core/services/ranking.service';
import { formatApiError } from '../../core/utils/mastermind.utils';
import { MastermindBoardComponent } from '../../shared/components/mastermind-board.component';

@Component({
  selector: 'app-ranking-detail-page',
  standalone: true,
  imports: [CommonModule, MastermindBoardComponent],
  template: `
    <section class="page-stack">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Ranking detalhado</p>
          <h2>Reprodução da partida ranqueada</h2>
        </div>
        <button type="button" class="secondary-button" (click)="goBack()">Voltar</button>
      </div>

      @if (loading()) {
        <section class="card-surface loading-card">Montando reprodução...</section>
      } @else if (error()) {
        <section class="card-surface feedback error">{{ error() }}</section>
      } @else {
        <app-mastermind-board [board]="board()" [revealSecret]="true" />
      }
    </section>
  `
})
export class RankingDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly rankingService = inject(RankingService);

  readonly loading = signal(true);
  readonly error = signal('');
  readonly board = signal<GameBoardState | null>(null);

  constructor() {
    const uuid = this.route.snapshot.paramMap.get('uuid') ?? '';
    this.rankingService
      .getRankingDetail(uuid)
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (board) => this.board.set(board),
        error: (error) => this.error.set(formatApiError(error, 'Não foi possível abrir a partida do ranking.'))
      });
  }

  goBack(): void {
    void this.router.navigate(['/app/ranking']);
  }
}