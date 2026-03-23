import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { LoginPageComponent } from './login-page.component';

describe('LoginPageComponent', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginPageComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
  });

  it('should submit login credentials and navigate to play page', () => {
    authService.login.and.returnValue(of(void 0));
    const fixture = TestBed.createComponent(LoginPageComponent);
    const component = fixture.componentInstance;

    component.form.setValue({ username: 'player', password: '123456' });
    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    expect(authService.login).toHaveBeenCalledWith({ username: 'player', password: '123456' });
    expect(router.navigate).toHaveBeenCalledWith(['/app/play']);
  });
});