import { TestBed } from '@angular/core/testing';
import { RulesPageComponent } from './rules-page.component';

describe('RulesPageComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RulesPageComponent]
    }).compileComponents();
  });

  it('should render the rules page heading', () => {
    const fixture = TestBed.createComponent(RulesPageComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Como funciona o Mastermind');
    expect(fixture.nativeElement.textContent).toContain('Regras por nível');
  });
});