import { CommonModule } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { finalize } from 'rxjs';
import {
  GameBoardState,
  GameLevel,
  GameStatus,
  GuessRow,
} from '../../core/models/mastermind.models';
import { GameService } from '../../core/services/game.service';
import {
  colorTokens,
  formatApiError,
  levelLabels,
  levelOptions,
  nextGuessSelection,
  removeGuessSelection,
  statusLabels,
} from '../../core/utils/mastermind.utils';
import { MastermindBoardComponent } from '../../shared/components/mastermind-board.component';

@Component({
  selector: 'app-play-page',
  standalone: true,
  imports: [CommonModule, MastermindBoardComponent],
  template: `
    <section class="page-stack">
      <div class="section-heading">
        <div>
          <p class="eyebrow">Jogar</p>
          <h2>Partida atual</h2>
        </div>
        @if (board()) {
          <span class="status-pill" [ngClass]="statusClasses[board()!.status]" >{{ statusLabels[board()!.status] }}</span>
        }
      </div>

      @if (loading()) {
        <section class="card-surface loading-card">
          Carregando tabuleiro...
        </section>
      } @else if (error()) {
        <section class="card-surface feedback error">{{ error() }}</section>
      } @else if (!board()) {
        <section class="card-surface empty-state">
          <h3>Nenhum jogo em andamento</h3>
          <p>Inicie uma partida e se desafie!</p>
        </section>

        <div class="level-grid">
          @for (option of levelOptions; track option.level) {
            <button
              type="button"
              class="level-card card-surface"
              (click)="openConfirmDialog('start', option.level)"
              [disabled]="loadingAction()"
            >
              <p class="eyebrow">{{ option.label }}</p>
              <strong>{{ option.columns }} cores</strong>
              <p>{{ option.description }}</p>
            </button>
          }
        </div>
      } @else {
        <div class="page-grid">
          <app-mastermind-board
            [board]="board()"
            [revealSecret]="board()!.status !== 'IN_PROGRESS'"
            [activeGuess]="activeGuess()"
            [readonly]="board()!.status !== 'IN_PROGRESS'"
            [slotCleared]="removeGuessAt.bind(this)"
          />

          <section class="card-surface side-panel">
            <div class="section-heading compact">
              <div>
                <p class="eyebrow">Config da rodada</p>
                <h3>{{ levelLabels[board()!.gameLevel] }}</h3>
              </div>
            </div>

            <ul class="stat-list">
              <li>
                <span>Casas por linha</span>
                <strong>{{ board()!.numberOfColumnColors }}</strong>
              </li>
              <li>
                <span>Tentativas máximas</span>
                <strong>{{ board()!.maximumOfAttempts }}</strong>
              </li>
              <li>
                <span>Cores repetidas</span>
                <strong>{{
                  board()!.repeatedColorAllowed ? 'Sim' : 'Não'
                }}</strong>
              </li>
            </ul>

            @if (board()!.status === 'IN_PROGRESS') {
              <div class="palette-grid">
                @for (color of colorTokens; track color) {
                  <button
                    type="button"
                    class="palette-button"
                    [class.disabled-color]="isColorDisabled(color)"
                    [class]="'peg-color-' + color"
                    [disabled]="isColorDisabled(color) || loadingAction()"
                    (click)="addColor(color)"
                  ></button>
                }
              </div>

              <div class="action-row">
                <button
                  type="button"
                  class="primary-button"
                  [disabled]="!canSubmitGuess() || loadingAction()"
                  (click)="openConfirmDialog('submit')"
                >
                  {{ loadingAction() ? 'Enviando...' : 'Tentar' }}
                </button>
                <button
                  type="button"
                  class="danger-button"
                  [disabled]="loadingAction()"
                  (click)="openConfirmDialog('give-up')"
                >
                  Desistir
                </button>
              </div>
            } @else {
              <div class="feedback success">
                Partida encerrada. O segredo foi revelado no topo do tabuleiro.
              </div>
              <button
                type="button"
                class="secondary-button full-width"
                (click)="backToMenu()"
              >
                Voltar ao menu
              </button>
            }
          </section>
        </div>
      }

      @if (confirmDialogOpen()) {
        <div class="dialog-backdrop" (click)="closeConfirmDialog()">
          <section
            class="dialog-panel card-surface"
            role="dialog"
            aria-modal="true"
            [attr.aria-label]="confirmDialogTitle()"
            (click)="$event.stopPropagation()"
          >
            <div class="dialog-copy">
              <p class="eyebrow">Confirmação</p>
              <h3>{{ confirmDialogTitle() }}</h3>
              <p class="dialog-message">{{ confirmDialogMessage() }}</p>
            </div>

            <div class="dialog-actions">
              <button
                type="button"
                class="secondary-button"
                (click)="closeConfirmDialog()"
              >
                Voltar
              </button>
              <button
                type="button"
                class="primary-button"
                (click)="confirmDialogAction()"
              >
                Confirmar
              </button>
            </div>
          </section>
        </div>
      }
    </section>
  `,
})
export class PlayPageComponent {

  statusClasses: Record<GameStatus, string> = {
    IN_PROGRESS: 'status-pill-in-progress',
    WON: 'status-pill-won',
    LOST: 'status-pill-lost',
    GAVE_UP: 'status-pill-gave-up',
  };

  private readonly gameService = inject(GameService);

  readonly board = signal<GameBoardState | null>(null);
  readonly activeGuess = signal<number[]>([]);
  readonly loading = signal(true);
  readonly loadingAction = signal(false);
  readonly error = signal('');
  readonly confirmDialogOpen = signal(false);
  readonly confirmDialogTitle = signal('');
  readonly confirmDialogMessage = signal('');
  readonly levelOptions = levelOptions;
  readonly colorTokens = colorTokens;
  readonly levelLabels = levelLabels;
  readonly statusLabels = statusLabels;
  readonly canSubmitGuess = computed(
    () =>
      this.activeGuess().length ===
      (this.board()?.numberOfColumnColors ?? Number.POSITIVE_INFINITY),
  );
  private pendingConfirmAction: 'start' | 'submit' | 'give-up' | null = null;
  private pendingStartLevel: GameLevel | null = null;

  constructor() {
    this.loadCurrentGame();
  }

  loadCurrentGame(): void {
    this.loading.set(true);
    this.error.set('');

    this.gameService
      .getCurrentGame()
      .pipe(finalize(() => this.loading.set(false)))
      .subscribe({
        next: (board) => {
          this.board.set(board);
          this.activeGuess.set([]);
        },
        error: (error) => {
          this.error.set(
            formatApiError(
              error,
              'Não foi possível consultar a partida atual.',
            ),
          );
        },
      });
  }

  createGame(level: GameLevel): void {
    this.loadingAction.set(true);
    this.error.set('');

    this.gameService
      .createGame(level)
      .pipe(finalize(() => this.loadingAction.set(false)))
      .subscribe({
        next: (board) => {
          this.board.set(board);
          this.activeGuess.set([]);
        },
        error: (error) => {
          this.error.set(
            formatApiError(error, 'Não foi possível criar a partida.'),
          );
        },
      });
  }

  addColor(color: number): void {
    const currentBoard = this.board();
    if (!currentBoard) {
      return;
    }

    this.activeGuess.set(
      nextGuessSelection(
        this.activeGuess(),
        color,
        currentBoard.numberOfColumnColors,
        currentBoard.repeatedColorAllowed,
      ),
    );
  }

  removeGuessAt(index: number): void {
    this.activeGuess.set(removeGuessSelection(this.activeGuess(), index));
  }

  isColorDisabled(color: number): boolean {
    const currentBoard = this.board();
    if (!currentBoard?.repeatedColorAllowed) {
      return this.activeGuess().includes(color);
    }

    return false;
  }

  openConfirmDialog(action: 'start' | 'submit' | 'give-up', level?: GameLevel): void {
    const currentBoard = this.board();

    if (action === 'start') {
      if (!level) {
        return;
      }

      this.pendingConfirmAction = 'start';
      this.pendingStartLevel = level;
      this.confirmDialogTitle.set('Iniciar partida');
      this.confirmDialogMessage.set('Deseja realmente iniciar uma nova partida?');
      this.confirmDialogOpen.set(true);
      return;
    }

    if (!currentBoard) {
      return;
    }

    if (action === 'submit' && !this.canSubmitGuess()) {
      return;
    }

    if (action === 'give-up' && currentBoard.rows.length === 0) {
      this.giveUp();
      return;
    }

    this.pendingConfirmAction = action;
    this.pendingStartLevel = null;
    this.confirmDialogTitle.set(
      action === 'submit' ? 'Confirmar tentativa' : 'Confirmar desistência',
    );
    this.confirmDialogMessage.set(
      action === 'submit'
        ? 'Deseja enviar essa tentativa agora?'
        : 'Deseja realmente desistir da partida atual?',
    );
    this.confirmDialogOpen.set(true);
  }

  closeConfirmDialog(): void {
    this.confirmDialogOpen.set(false);
    this.pendingConfirmAction = null;
    this.pendingStartLevel = null;
  }

  confirmDialogAction(): void {
    const action = this.pendingConfirmAction;
    this.confirmDialogOpen.set(false);
    this.pendingConfirmAction = null;

    if (action === 'start' && this.pendingStartLevel) {
      const level = this.pendingStartLevel;
      this.pendingStartLevel = null;
      this.createGame(level);
      return;
    }

    this.pendingStartLevel = null;

    if (action === 'submit') {
      this.submitGuess();
      return;
    }

    if (action === 'give-up') {
      this.giveUp();
    }
  }

  submitGuess(): void {
    const currentBoard = this.board();
    const guess = this.activeGuess();

    if (!currentBoard || !this.canSubmitGuess()) {
      return;
    }

    this.loadingAction.set(true);
    this.error.set('');

    this.gameService
      .submitGuess(guess)
      .pipe(finalize(() => this.loadingAction.set(false)))
      .subscribe({
        next: (result) => {
          const appendedRow = this.buildGuessRow(
            guess,
            result.tips,
            currentBoard.numberOfColumnColors,
            result.status,
          );
          const nextRows = [...currentBoard.rows, appendedRow];

          this.board.set({
            ...currentBoard,
            rows: nextRows,
            status: result.status,
            secret: result.secret ?? currentBoard.secret,
          });
          this.activeGuess.set([]);
        },
        error: (error) => {
          this.error.set(
            formatApiError(error, 'Não foi possível enviar a tentativa.'),
          );
        },
      });
  }

  giveUp(): void {
    if (!this.board()) {
      return;
    }

    this.loadingAction.set(true);
    this.error.set('');

    this.gameService
      .giveUp()
      .pipe(finalize(() => this.loadingAction.set(false)))
      .subscribe({
        next: (board) => {
          this.board.update((currentBoard) => ({
            ...(currentBoard ?? board),
            ...board,
            rows: currentBoard?.rows ?? board.rows,
          }));
          this.activeGuess.set([]);
        },
        error: (error) => {
          this.error.set(
            formatApiError(error, 'Não foi possível desistir da partida.'),
          );
        },
      });
  }

  backToMenu(): void {
    this.board.set(null);
    this.activeGuess.set([]);
    this.error.set('');
  }

  private buildGuessRow(
    guess: number[],
    tips: GuessRow['tips'] | undefined,
    total: number,
    status: GameBoardState['status'],
  ): GuessRow {
    if (tips) {
      return { guess, tips };
    }

    if (status === 'WON') {
      return {
        guess,
        tips: { correctPositions: total, correctColors: 0 },
      };
    }

    return {
      guess,
      tips: { correctPositions: 0, correctColors: 0 },
    };
  }
}
