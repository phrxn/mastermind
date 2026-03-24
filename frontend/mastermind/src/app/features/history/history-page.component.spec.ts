import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { HistoryResponse, HistorySummaryItem } from '../../core/models/mastermind.models';
import { HistoryService } from '../../core/services/history.service';
import { HistoryPageComponent } from './history-page.component';

describe('HistoryPageComponent', () => {
  let historyService: jasmine.SpyObj<HistoryService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    historyService = jasmine.createSpyObj<HistoryService>('HistoryService', ['getHistory']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));

    const fullHistory: HistorySummaryItem[] = Array.from({ length: 11 }, (_, index) => ({
      publicUuid: `game-${index + 1}`,
      level: 'EASY',
      pointsMaked: 20 - index,
      status: 'WON',
      attemptsUsed: index + 1,
      createdAt: null,
      finishedAt: null
    }));
    const response: HistoryResponse = {
      gameHistoryBestGames: [
        { publicUuid: 'best-1', level: 'EASY', pointsMaked: 20, status: 'WON', attemptsUsed: 2, createdAt: null, finishedAt: null }
      ],
      gameHistoryFull: fullHistory
    };

    historyService.getHistory.and.returnValue(of(response));

    await TestBed.configureTestingModule({
      imports: [HistoryPageComponent],
      providers: [
        { provide: HistoryService, useValue: historyService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();
  });

  it('should paginate history entries and open a selected detail', () => {
    const fixture = TestBed.createComponent(HistoryPageComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Página 1 de 2');

    const nextButton = Array.from(fixture.nativeElement.querySelectorAll('.pagination-bar button'))[1] as HTMLButtonElement;
    nextButton.click();
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Página 2 de 2');

    const historyCard = fixture.nativeElement.querySelector('.history-card') as HTMLButtonElement;
    historyCard.click();
    expect(router.navigate).toHaveBeenCalled();
  });
});