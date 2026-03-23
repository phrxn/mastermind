import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { AppShellComponent } from './app-shell.component';

describe('AppShellComponent', () => {
  const authService = {
    username: () => 'tester',
    logout: jasmine.createSpy('logout')
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppShellComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    }).compileComponents();
  });

  it('should toggle the mobile menu and call logout', () => {
    const fixture = TestBed.createComponent(AppShellComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();

    expect(component.menuOpen()).toBeFalse();
    (fixture.nativeElement.querySelector('.menu-toggle') as HTMLButtonElement).click();
    expect(component.menuOpen()).toBeTrue();

    (fixture.nativeElement.querySelector('.logout-button') as HTMLButtonElement).click();
    expect(authService.logout).toHaveBeenCalledWith(true);
  });
});