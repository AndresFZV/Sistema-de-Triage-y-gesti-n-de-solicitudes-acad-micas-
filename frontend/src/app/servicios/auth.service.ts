import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoginRequest, TokenResponse } from '../modelos/auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  isAuthenticated = signal(!!localStorage.getItem('token'));

  login(request: LoginRequest): Observable<TokenResponse> {
    return this.http.post<TokenResponse>(`${this.apiUrl}/login`, request);
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('roles');
    this.isAuthenticated.set(false);
  }

  guardarToken(response: TokenResponse): void {
    localStorage.setItem('token', response.token);
    localStorage.setItem('refreshToken', response.refreshToken);
    localStorage.setItem('roles', JSON.stringify(response.roles));
    this.isAuthenticated.set(true);
  }

  obtenerToken(): string | null {
    return localStorage.getItem('token');
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

obtenerRoles(): string[] {
  const roles = localStorage.getItem('roles');
  return roles ? JSON.parse(roles) : [];
}

esAdmin(): boolean {
  return this.obtenerRoles().includes('ADMIN');
}

esAdministrativo(): boolean {
  return this.obtenerRoles().includes('ADMINISTRATIVO');
}

esEstudiante(): boolean {
  return this.obtenerRoles().includes('ESTUDIANTE');
}

esDocente(): boolean {
  return this.obtenerRoles().includes('DOCENTE');
}

obtenerIdUsuario(): string | null {
  return localStorage.getItem('userId');
}

obtenerIdDesdeToken(): string | null {
  const token = localStorage.getItem('token');
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub; // 'sub' es el email del usuario
  } catch {
    return null;
  }
}

obtenerEmailDesdeToken(): string | null {
  const token = localStorage.getItem('token');
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    return payload.sub;
  } catch {
    return null;
  }
}

cerrarSesion(): void {
  this.logout();
}
  
}