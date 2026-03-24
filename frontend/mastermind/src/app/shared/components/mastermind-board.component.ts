import { CommonModule } from '@angular/common';
import { Component, computed, input } from '@angular/core';
import { GameBoardState } from '../../core/models/mastermind.models';
import { buildGuessSlots, buildTipsGrid, levelLabels } from '../../core/utils/mastermind.utils';

@Component({
  selector: 'app-mastermind-board',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (board()) {
      <section class="board card-surface">
        <div class="board-header">
          <div>
            <p class="eyebrow">Tabuleiro</p>
            <h2>{{ levelLabels[board()!.gameLevel] }}</h2>
          </div>
          <div class="board-meta">
            <span>{{ board()!.rows.length }}/{{ board()!.maximumOfAttempts }} tentativas</span>
            <span>{{ board()!.numberOfColumnColors }} cores</span>
          </div>
        </div>

        <div class="secret-row">
          @for (color of secretSlots(); track $index) {
            <button type="button" class="peg" [class.hidden]="color === null" [class]="pegClass(color)">
              @if (color === null) {
                ?
              }
            </button>
          }
        </div>

        <div class="rows-grid">
          @for (row of renderedRows(); track $index) {
            <div class="board-row" [class.current-row]="row.isCurrent">
              <div class="guess-row">
                @for (slot of row.guess; track $index) {
                  <button
                    type="button"
                    class="peg"
                    [class.empty]="slot === null"
                    [class.clickable]="row.isCurrent && slot !== null && !readonly()"
                    [class]="pegClass(slot)"
                    (click)="handleSlotClick(row.isCurrent, $index, slot)"
                  >
                    @if (slot === null) {
                      +
                    }
                  </button>
                }
              </div>

              <div class="tips-grid">
                @for (tip of row.tips; track $index) {
                  <span class="tip" [class.tip-position]="tip === 'position'" [class.tip-color]="tip === 'color'"></span>
                }
              </div>
            </div>
          }
        </div>
      </section>
    }
  `
})
export class MastermindBoardComponent {
  readonly levelLabels = levelLabels;
  readonly board = input<GameBoardState | null>(null);
  readonly revealSecret = input(false);
  readonly activeGuess = input<number[]>([]);
  readonly readonly = input(true);
  readonly slotCleared = input<(index: number) => void>();

  readonly secretSlots = computed(() => {
    const currentBoard = this.board();
    if (!currentBoard) {
      return [];
    }

    if (this.revealSecret() && currentBoard.secret?.length) {
      return currentBoard.secret;
    }

    return Array.from({ length: currentBoard.numberOfColumnColors }, () => null);
  });

  readonly renderedRows = computed(() => {
    const currentBoard = this.board();

    if (!currentBoard) {
      return [];
    }

    return Array.from({ length: currentBoard.maximumOfAttempts }, (_, index) => {
      const existingRow = currentBoard.rows[index];
      const isCurrent = !this.readonly() && !existingRow && index === currentBoard.rows.length;

      return {
        guess: existingRow
          ? buildGuessSlots(currentBoard.numberOfColumnColors, existingRow.guess)
          : isCurrent
            ? buildGuessSlots(currentBoard.numberOfColumnColors, this.activeGuess())
            : Array.from({ length: currentBoard.numberOfColumnColors }, () => null),
        tips: existingRow
          ? buildTipsGrid(existingRow.tips, currentBoard.numberOfColumnColors)
          : Array.from({ length: currentBoard.numberOfColumnColors }, () => 'empty' as const),
        isCurrent
      };
    });
  });

  pegClass(color: number | null): string {
    return color === null ? '' : `peg-color-${color}`;
  }

  handleSlotClick(isCurrent: boolean, index: number, slot: number | null): void {
    if (!isCurrent || this.readonly() || slot === null) {
      return;
    }

    this.slotCleared()?.(index);
  }
}