// src/app/services/auth.service.spec.ts

import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { of } from 'rxjs';
import { AuthService, UserProfile } from './auth-service';
import { LoginService } from './user.service';


// --- Dublês (Mocks) para as dependências ---
const mockLoginService = {
  login: jasmine.createSpy('login').and.returnValue(of({ token: 'fake-token', name: 'Test User' })),
  getUserProfile: jasmine.createSpy('getUserProfile').and.returnValue(of({ id: 1, name: 'Test User', email: 'test@test.com' } as UserProfile))
};

const mockRouter = {
  navigate: jasmine.createSpy('navigate')
};


describe('AuthService', () => {
  let service: AuthService;
  let loginService: LoginService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      // Fornece os Mocks em vez dos serviços reais
      providers: [
        AuthService,
        { provide: LoginService, useValue: mockLoginService },
        { provide: Router, useValue: mockRouter }
      ]
    });

    // Pega as instâncias que o TestBed criou para nós
    service = TestBed.inject(AuthService);
    loginService = TestBed.inject(LoginService);
    router = TestBed.inject(Router);

    // Limpa o sessionStorage e reseta os spies antes de cada teste
    sessionStorage.clear();
    mockLoginService.login.calls.reset();
    mockLoginService.getUserProfile.calls.reset();
    mockRouter.navigate.calls.reset();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call login service, save token, and fetch profile on login', (done) => {
    const email = 'test@test.com';
    const password = '123';

    service.login(email, password).subscribe(response => {
      expect(response.token).toBe('fake-token');
      expect(loginService.login).toHaveBeenCalledWith(email, password);
      expect(sessionStorage.getItem('auth-token')).toBe('fake-token');
      expect(loginService.getUserProfile).toHaveBeenCalled();

      service.isLoggedIn$.subscribe(isLoggedIn => {
        expect(isLoggedIn).toBeTrue();
      });

      service.currentUser$.subscribe(user => {
        expect(user?.name).toBe('Test User');
        done();
      });
    });
  });

  it('should clear session storage and update state on logout', () => {
    // Simula um estado logado
    sessionStorage.setItem('auth-token', 'some-token');
    (service as any)._isLoggedIn$.next(true);

    service.logout();

    expect(sessionStorage.getItem('auth-token')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);

    service.isLoggedIn$.subscribe(isLoggedIn => {
      expect(isLoggedIn).toBeFalse();
    });
  });
});