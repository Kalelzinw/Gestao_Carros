// src/app/services/auth.service.ts

import { Injectable, inject } from '@angular/core';
import { BehaviorSubject, Observable, tap, catchError, of } from 'rxjs';
import { Router } from '@angular/router';
import { LoginService } from './user.service'; // Importa o serviço de API
import { loginResponse } from '../types/login-response.type';

// Podemos definir a interface do perfil aqui
export interface UserProfile {
  id: number;
  fotoUrl: string | null;
  name: string;
  email: string;
  tel: string;
  dateBorn: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Observables para o estado da aplicação
  private _isLoggedIn$ = new BehaviorSubject<boolean>(false);
  public isLoggedIn$ = this._isLoggedIn$.asObservable();

  private _currentUser$ = new BehaviorSubject<UserProfile | null>(null);
  public currentUser$ = this._currentUser$.asObservable();

  // Injeção de dependências
  private loginService = inject(LoginService);
  private router = inject(Router);

  constructor() {
    this.checkTokenOnLoad();
  }

  // Verifica o token ao carregar a aplicação
  private checkTokenOnLoad(): void {
    const token = this.getToken();
    if (token) {
      this._isLoggedIn$.next(true);
      this.fetchAndSetUserProfile();
    }
  }

  // --- MÉTODOS PÚBLICOS PARA OS COMPONENTES CHAMAREM ---

  // Método de Login
 login(email: string, password: string): Observable<loginResponse> {
  // 1. Pede ao LoginService para fazer a chamada.
  return this.loginService.login(email, password).pipe(
    tap(response => {
      // 2. QUANDO a resposta chegar, o AuthService faz o trabalho dele:
      // a. Salva o token no lugar certo.
      sessionStorage.setItem("auth-token", response.token);
      
      // b. Avisa a aplicação inteira que o usuário está logado.
      this._isLoggedIn$.next(true);

      // c. (Opcional, mas recomendado) Busca os dados do perfil para atualizar o nome/foto.
      this.loginService.getUserProfile().subscribe(profile => {
        this._currentUser$.next(profile);
      });
    })
  );
}
  // NOVO MÉTODO: Signup orquestrado pelo AuthService
  signup(formValues: any, fotoFile: File): Observable<loginResponse> {
    return this.loginService.signup(formValues, fotoFile).pipe(
      tap(response => this.handleAuthentication(response.token))
    );
  }

  // NOVO MÉTODO: Update orquestrado pelo AuthService
  updateUserProfile(formValues: any, fotoFile: File | null): Observable<UserProfile> {
    return this.loginService.updateUserProfile(formValues, fotoFile).pipe(
      tap(updatedProfile => {
        // Após o sucesso do update, atualiza o estado do usuário atual
        this._currentUser$.next(updatedProfile);
      })
    );
  }

  // NOVO MÉTODO: Delete orquestrado pelo AuthService
  deleteUserProfile(): Observable<void> {
    return this.loginService.deleteUserProfile().pipe(
      tap(() => {
        // Após deletar no backend, executa a lógica completa de logout
        this.logout();
      })
    );
  }

  // --- MÉTODOS DE CONTROLE INTERNO ---

  // Lógica de logout centralizada
  logout(): void {
    sessionStorage.removeItem("auth-token");
    this._isLoggedIn$.next(false);
    this._currentUser$.next(null);
    this.router.navigate(['/login']);
  }

  // Busca o token do storage
  getToken(): string | null {
    return sessionStorage.getItem('auth-token');
  }

  // Busca e define o perfil do usuário no estado da aplicação
  private fetchAndSetUserProfile(): void {
    this.loginService.getUserProfile().pipe(
      catchError(() => {
        this.logout(); // Se o token for inválido, faz logout
        return of(null);
      })
    ).subscribe(profile => {
      if (profile) this._currentUser$.next(profile);
    });
  }

  // Centraliza a lógica que acontece após login ou signup
  private handleAuthentication(token: string): void {
    sessionStorage.setItem("auth-token", token);
    this._isLoggedIn$.next(true);
    this.fetchAndSetUserProfile();
  }
}