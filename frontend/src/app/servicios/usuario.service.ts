import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UsuarioResponse, CrearUsuarioRequest } from '../dto/usuario.dto';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {

  private apiUrl = 'http://localhost:8080/api/usuarios';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private headers(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.obtenerToken()}`
    });
  }

  listar(): Observable<UsuarioResponse[]> {
    return this.http.get<UsuarioResponse[]>(this.apiUrl, { headers: this.headers() });
  }

  obtener(id: string): Observable<UsuarioResponse> {
    return this.http.get<UsuarioResponse>(`${this.apiUrl}/${id}`, { headers: this.headers() });
  }

  crear(request: CrearUsuarioRequest): Observable<UsuarioResponse> {
    return this.http.post<UsuarioResponse>(this.apiUrl, request, { headers: this.headers() });
  }

  actualizar(id: string, request: any): Observable<UsuarioResponse> {
    return this.http.put<UsuarioResponse>(`${this.apiUrl}/${id}`, request, { headers: this.headers() });
  }

  eliminar(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, { headers: this.headers() });
  }
}