import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, TokenResponse } from '../dto/auth.dto';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/login`, request);
  }

  logout(): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/logout`, {});
  }

  guardarToken(response: TokenResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('refreshToken', response.refreshToken);
    localStorage.setItem('roles', JSON.stringify(response.roles));
  }

  obtenerToken(): string | null {
    return localStorage.getItem('token');
  }

  obtenerRoles(): string[] {
    const roles = localStorage.getItem('roles');
    return roles ? JSON.parse(roles) : [];
  }

  estaAutenticado(): boolean {
    return !!this.obtenerToken();
  }

  esAdmin(): boolean {
    return this.obtenerRoles().includes('ADMIN');
  }

  cerrarSesion(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('roles');
  }
}