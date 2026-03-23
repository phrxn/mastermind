import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { SignupPageComponent } from './signup-page.component';

describe('SignupPageComponent', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['signup']);

    await TestBed.configureTestingModule({
      imports: [SignupPageComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
      ]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
  });

  it('should open a confirmation dialog before sending the signup request', () => {
    authService.signup.and.returnValue(of(void 0));
    const fixture = TestBed.createComponent(SignupPageComponent);
    const component = fixture.componentInstance;

    component.form.setValue({
      name: 'Player One',
      email: 'player@example.com',
      nickname: 'player1',
      age: 24,
      password: '123456'
    });
    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(component.confirmDialogOpen()).toBeTrue();
    expect(authService.signup).not.toHaveBeenCalled();
    expect(fixture.nativeElement.querySelector('.dialog-panel')).not.toBeNull();
  });

  it('should submit after confirmation and redirect to login', fakeAsync(() => {
    authService.signup.and.returnValue(of(void 0));
    const fixture = TestBed.createComponent(SignupPageComponent);
    const component = fixture.componentInstance;

    component.form.setValue({
      name: 'Player One',
      email: 'player@example.com',
      nickname: 'player1',
      age: 24,
      password: '123456'
    });
    component.openConfirmDialog();
    fixture.detectChanges();

    const confirmButton = Array.from(fixture.nativeElement.querySelectorAll('.dialog-panel button'))[1] as HTMLButtonElement;
    confirmButton.click();
    fixture.detectChanges();
    tick(900);

    expect(authService.signup).toHaveBeenCalledWith({
      name: 'Player One',
      email: 'player@example.com',
      nickname: 'player1',
      age: 24,
      password: '123456'
    });
    expect(component.success()).toContain('Conta criada');
    expect(router.navigate).toHaveBeenCalledWith(['/auth/login']);
  }));
});