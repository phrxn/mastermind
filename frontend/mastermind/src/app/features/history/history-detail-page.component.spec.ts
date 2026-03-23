import { TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, Router } from '@angular/router';
import { of } from 'rxjs';
import { HistoryService } from '../../core/services/history.service';
import { HistoryDetailPageComponent } from './history-detail-page.component';

describe('HistoryDetailPageComponent', () => {
  let historyService: jasmine.SpyObj<HistoryService>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    historyService = jasmine.createSpyObj<HistoryService>('HistoryService', ['getHistoryDetail']);
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);
    router.navigate.and.returnValue(Promise.resolve(true));
    historyService.getHistoryDetail.and.returnValue(
      of({
        status: 'WON',
        gameLevel: 'EASY',
        numberOfColumnColors: 4,
        maximumOfAttempts: 10,
        repeatedColorAllowed: false,
        rows: [],
        secret: [1, 2, 3, 4]
      })
    );

    await TestBed.configureTestingModule({
      imports: [HistoryDetailPageComponent],
      providers: [
        { provide: HistoryService, useValue: historyService },
        { provide: Router, useValue: router },
        { provide: ActivatedRoute, useValue: { snapshot: { paramMap: convertToParamMap({ uuid: 'history-1' }) } } }
      ]
    }).compileComponents();
  });

  it('should load the history board and navigate back', () => {
    const fixture = TestBed.createComponent(HistoryDetailPageComponent);
    fixture.detectChanges();

    expect(historyService.getHistoryDetail).toHaveBeenCalledWith('history-1');

    const backButton = fixture.nativeElement.querySelector('.secondary-button') as HTMLButtonElement;
    backButton.click();

    expect(router.navigate).toHaveBeenCalledWith(['/app/history']);
  });
});