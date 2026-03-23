import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { Router } from '@angular/router';
import { RankingService } from '../../core/services/ranking.service';
import { RankingPageComponent } from './ranking-page.component';

describe('RankingPageComponent', () => {
  let rankingService: jasmine.SpyObj<RankingService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    rankingService = jasmine.createSpyObj<RankingService>('RankingService', ['getRanking']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));
    rankingService.getRanking.and.returnValue(
      of({
        top10EasyGames: [
          {
            gameUuidPublic: 'easy-1',
            userNickname: 'Alpha',
            gameLevel: 'EASY',
            pointsMaked: 42,
            attemptsUsed: 3,
            createdAt: '2026-03-20T10:00:00Z',
            finishedAt: '2026-03-20T10:03:00Z'
          },
          {
            gameUuidPublic: 'easy-2',
            userNickname: 'Beta',
            gameLevel: 'EASY',
            pointsMaked: 35,
            attemptsUsed: 4,
            createdAt: '2026-03-20T11:00:00Z',
            finishedAt: '2026-03-20T11:04:00Z'
          }
        ],
        top10NormalGames: [
          {
            gameUuidPublic: 'normal-1',
            userNickname: 'Gamma',
            gameLevel: 'NORMAL',
            pointsMaked: 30,
            attemptsUsed: 5,
            createdAt: null,
            finishedAt: null
          }
        ],
        top10HardGames: [],
        top10MastermindGames: []
      })
    );

    await TestBed.configureTestingModule({
      imports: [RankingPageComponent],
      providers: [
        { provide: RankingService, useValue: rankingService },
        { provide: Router, useValue: router }
      ]
    }).compileComponents();
  });

  it('should render ranking cards as a vertical list and highlight the leader', () => {
    const fixture = TestBed.createComponent(RankingPageComponent);
    fixture.detectChanges();

    const cards = Array.from(fixture.nativeElement.querySelectorAll('.ranking-card')) as HTMLElement[];

    expect(cards.length).toBe(2);
    expect(cards[0].classList.contains('top-rank-1')).toBeTrue();
    expect(cards[0].textContent).toContain('Alpha');
    expect(cards[1].textContent).toContain('Beta');
  });

  it('should switch tabs and open the selected ranking detail', () => {
    const fixture = TestBed.createComponent(RankingPageComponent);
    fixture.detectChanges();

    const tabButtons = Array.from(fixture.nativeElement.querySelectorAll('.tab-button')) as HTMLButtonElement[];
    const normalTab = tabButtons.find((button) => button.textContent?.includes('Normal')) as HTMLButtonElement;
    normalTab.click();
    fixture.detectChanges();

    const card = fixture.nativeElement.querySelector('.ranking-card') as HTMLButtonElement;
    expect(card.textContent).toContain('Gamma');

    card.click();
    expect(router.navigate).toHaveBeenCalledWith(['/app/ranking', 'normal-1']);
  });
});