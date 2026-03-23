import { TestBed } from '@angular/core/testing';
import { MastermindBoardComponent } from './mastermind-board.component';

describe('MastermindBoardComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MastermindBoardComponent]
    }).compileComponents();
  });

  it('should render current row slots and clear a slot when clicked', () => {
    const fixture = TestBed.createComponent(MastermindBoardComponent);
    const slotCleared = jasmine.createSpy('slotCleared');

    fixture.componentRef.setInput('board', {
      status: 'IN_PROGRESS',
      gameLevel: 'EASY',
      numberOfColumnColors: 4,
      maximumOfAttempts: 2,
      repeatedColorAllowed: false,
      rows: []
    });
    fixture.componentRef.setInput('readonly', false);
    fixture.componentRef.setInput('activeGuess', [1, 2]);
    fixture.componentRef.setInput('slotCleared', slotCleared);
    fixture.detectChanges();

    const clickablePegs = Array.from(fixture.nativeElement.querySelectorAll('.peg.clickable')) as HTMLButtonElement[];
    expect(clickablePegs.length).toBe(2);

    clickablePegs[0].click();
    expect(slotCleared).toHaveBeenCalledWith(0);
  });
});