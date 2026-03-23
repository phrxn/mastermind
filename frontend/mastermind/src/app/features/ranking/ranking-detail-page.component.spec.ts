import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of } from 'rxjs';
import { RankingService } from '../../core/services/ranking.service';
import { RankingDetailPageComponent } from './ranking-detail-page.component';

describe('RankingDetailPageComponent', () => {
  let rankingService: jasmine.SpyObj<RankingService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    rankingService = jasmine.createSpyObj<RankingService>('RankingService', ['getRankingDetail']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));
    rankingService.getRankingDetail.and.returnValue(
      of({
        status: 'WON',
        gameLevel: 'NORMAL',
        numberOfColumnColors: 4,
        maximumOfAttempts: 10,
        repeatedColorAllowed: true,
        rows: [],
        secret: [1, 2, 3, 4]
      })
    );

    await TestBed.configureTestingModule({
      imports: [RankingDetailPageComponent],
      providers: [
        { provide: RankingService, useValue: rankingService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ uuid: 'ranking-1' }) } } }
      ]
    }).compileComponents();
  });

  it('should load the ranking board and navigate back', () => {
    const fixture = TestBed.createComponent(RankingDetailPageComponent);
    fixture.detectChanges();

    expect(rankingService.getRankingDetail).toHaveBeenCalledWith('ranking-1');

    const backButton = fixture.nativeElement.querySelector('.secondary-button') as HTMLButtonElement;
    backButton.click();

    expect(router.navigate).toHaveBeenCalledWith(['/app/ranking']);
  });
});