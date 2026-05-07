import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SolicitudResponse, CrearSolicitudRequest, PaginaResponse } from '../dto/solicitud.dto';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class SolicitudService {

  private apiUrl = 'http://localhost:8080/api/solicitudes';

  constructor(private http: HttpClient, private authService: AuthService) {}

  private headers(): HttpHeaders {
    return new HttpHeaders({
      'Authorization': `Bearer ${this.authService.obtenerToken()}`
    });
  }

  listar(filtros: any = {}): Observable<PaginaResponse<SolicitudResponse>> {
    let params = new HttpParams();
    if (filtros.estado) params = params.set('estado', filtros.estado);
    if (filtros.tipo) params = params.set('tipo', filtros.tipo);
    if (filtros.prioridad) params = params.set('prioridad', filtros.prioridad);
    if (filtros.page !== undefined) params = params.set('page', filtros.page);
    if (filtros.size !== undefined) params = params.set('size', filtros.size);
    return this.http.get<PaginaResponse<SolicitudResponse>>(this.apiUrl, { headers: this.headers(), params });
  }

  obtener(codigo: string): Observable<SolicitudResponse> {
    return this.http.get<SolicitudResponse>(`${this.apiUrl}/${codigo}`, { headers: this.headers() });
  }

  crear(request: CrearSolicitudRequest): Observable<SolicitudResponse> {
    return this.http.post<SolicitudResponse>(this.apiUrl, request, { headers: this.headers() });
  }

  clasificar(codigo: string, tipoSolicitud: string, adminId: string): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(
      `${this.apiUrl}/${codigo}/clasificar`,
      { tipoSolicitud, adminId },
      { headers: this.headers() }
    );
  }

  enRevision(codigo: string, responsableId: string): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(
      `${this.apiUrl}/${codigo}/revision`,
      { responsableId },
      { headers: this.headers() }
    );
  }

  atender(codigo: string, adminId: string): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(
      `${this.apiUrl}/${codigo}/atender`,
      { adminId },
      { headers: this.headers() }
    );
  }

  rechazar(codigo: string, adminId: string): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(
      `${this.apiUrl}/${codigo}/rechazar`,
      { adminId },
      { headers: this.headers() }
    );
  }

  cerrar(codigo: string, adminId: string): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(
      `${this.apiUrl}/${codigo}/cerrar`,
      { adminId },
      { headers: this.headers() }
    );
  }

  cancelar(codigo: string, solicitanteId: string): Observable<SolicitudResponse> {
    return this.http.patch<SolicitudResponse>(
      `${this.apiUrl}/${codigo}/cancelar`,
      { solicitanteId },
      { headers: this.headers() }
    );
  }

  historial(codigo: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${codigo}/historial`, { headers: this.headers() });
  }

  dashboard(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/dashboard`, { headers: this.headers() });
  }
}