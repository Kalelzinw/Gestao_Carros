import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor() {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    
    // MUDANÇA: Buscando do sessionStorage com a chave correta 'auth-token'
    const token = sessionStorage.getItem('auth-token'); 

    // A lógica de verificação da URL (continua correta e importante)
    if (request.url.includes('/auth/login') || request.url.includes('/auth/register') || !token) {
      console.log('Interceptor: Rota pública ou sem token, passando direto:', request.url);
      return next.handle(request);
    }

    // O resto do código para adicionar o header (continua correto)
    const authReq = request.clone({
      headers: request.headers.set('Authorization', `Bearer ${token}`)
    });

    console.log('Interceptor: Token JWT adicionado ao cabeçalho da requisição:', authReq.url);
    return next.handle(authReq);
  }
}