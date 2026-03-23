import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { GameService } from '../../core/services/game.service';
import { PlayPageComponent } from './play-page.component';

describe('PlayPageComponent', () => {
  let gameService: jasmine.SpyObj<GameService>;

  beforeEach(async () => {
    gameService = jasmine.createSpyObj<GameService>('GameService', ['getCurrentGame', 'createGame', 'submitGuess', 'giveUp']);
    gameService.getCurrentGame.and.returnValue(
      of({
        status: 'IN_PROGRESS',
        gameLevel: 'EASY',
        numberOfColumnColors: 4,
        maximumOfAttempts: 10,
        repeatedColorAllowed: false,
        rows: []
      })
    );
    gameService.submitGuess.and.returnValue(of({ status: 'IN_PROGRESS', gameLevel: 'EASY', tips: { correctPositions: 1, correctColors: 1 } }));
    gameService.giveUp.and.returnValue(
      of({
        status: 'GAVE_UP',
        gameLevel: 'EASY',
        numberOfColumnColors: 4,
        maximumOfAttempts: 10,
        repeatedColorAllowed: false,
        rows: [],
        secret: [1, 2, 3, 4]
      })
    );

    await TestBed.configureTestingModule({
      imports: [PlayPageComponent],
      providers: [{ provide: GameService, useValue: gameService }]
    }).compileComponents();
  });

  it('should open a custom confirmation dialog before submitting a guess', () => {
    const fixture = TestBed.createComponent(PlayPageComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    component.activeGuess.set([1, 2, 3, 4]);
    fixture.detectChanges();

    const tryButton = fixture.nativeElement.querySelector('.primary-button') as HTMLButtonElement;
    tryButton.click();
    fixture.detectChanges();

    expect(component.confirmDialogOpen()).toBeTrue();
    expect(fixture.nativeElement.querySelector('.dialog-panel')).not.toBeNull();
    expect(gameService.submitGuess).not.toHaveBeenCalled();

    const confirmButton = Array.from(fixture.nativeElement.querySelectorAll('.dialog-panel button'))[1] as HTMLButtonElement;
    confirmButton.click();
    fixture.detectChanges();

    expect(gameService.submitGuess).toHaveBeenCalledWith([1, 2, 3, 4]);
  });

  it('should open a custom confirmation dialog before giving up', () => {
    const fixture = TestBed.createComponent(PlayPageComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    const giveUpButton = fixture.nativeElement.querySelector('.danger-button') as HTMLButtonElement;
    giveUpButton.click();
    fixture.detectChanges();

    expect(component.confirmDialogOpen()).toBeTrue();

    const confirmButton = Array.from(fixture.nativeElement.querySelectorAll('.dialog-panel button'))[1] as HTMLButtonElement;
    confirmButton.click();
    fixture.detectChanges();

    expect(gameService.giveUp).toHaveBeenCalled();
  });
});