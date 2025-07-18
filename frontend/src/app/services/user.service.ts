// src/app/services/login.service.ts

import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { loginResponse } from '../types/login-response.type';

@Injectable({
  providedIn: 'root'
})
export class LoginService { 
  private apiUrl = "http://localhost:8080/auth";
  private userapiUrl = "http://localhost:8080/user";

  constructor(private httpClient: HttpClient) {}

  // Este método apenas faz a chamada e retorna a resposta. Nada mais.
  login(email: string, password: string): Observable<loginResponse> {
  // A única responsabilidade dele é fazer a chamada e retornar a resposta.
  return this.httpClient.post<loginResponse>(`${this.apiUrl}/login`, { email, password });
}

  // Idem para o signup.
  signup(formValues: any, fotoFile: File): Observable<loginResponse> { 
    const data = new FormData();
    data.append('name', formValues.name);
    data.append('email', formValues.email);
    data.append('password', formValues.password);
    data.append('cpf', formValues.cpf.replace(/\D/g, ''));
    data.append('tel', formValues.tel.replace(/\D/g, ''));
    data.append('dateBorn', formValues.nasc);
    data.append('foto', fotoFile);
    return this.httpClient.post<loginResponse>(`${this.apiUrl}/register`, data);
  }

  // Todos os outros métodos apenas fazem a chamada para a API.
  updateUserProfile(formValues: any, fotoFile: File | null): Observable<any> {
    const data = new FormData();
    if (formValues.email) {
      data.append('email', formValues.email);
    }
    if (formValues.tel) {
      const telLimpo = formValues.tel.replace(/\D/g, '');
      data.append('tel', telLimpo);
    }
    if (formValues.password) {
      data.append('password', formValues.password);
    }
    if (fotoFile) {
      data.append('foto', fotoFile);
    }
    return this.httpClient.patch<any>(`${this.userapiUrl}/update`, data);
  }

  deleteUserProfile(): Observable<any> {
    return this.httpClient.delete<any>(`${this.userapiUrl}/delete`);
  }

  getUserProfile(): Observable<any> {
    return this.httpClient.get<any>(`${this.userapiUrl}/profile`);
  }
}