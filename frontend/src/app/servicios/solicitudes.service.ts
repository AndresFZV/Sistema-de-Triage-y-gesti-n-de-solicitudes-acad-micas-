import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map } from 'rxjs';
import { SolicitudResumen } from '../modelos/solicitudes';

@Injectable({
  providedIn: 'root'
})
export class SolicitudesService {

  private http = inject(HttpClient);
  private url = 'http://localhost:8080/api/solicitudes';

  listar() {
    return this.http.get<any>(this.url).pipe(
      map(response => (response?.contenido ?? []) as SolicitudResumen[])
    );
  }

  listarPorUsuario(usuarioId: string) {
    return this.http.get<SolicitudResumen[]>(
      `http://localhost:8080/api/usuarios/${usuarioId}/solicitudes`
    );
  }
}