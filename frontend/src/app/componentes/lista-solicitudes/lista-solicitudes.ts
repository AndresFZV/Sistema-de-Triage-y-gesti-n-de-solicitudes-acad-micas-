import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SlicePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Card } from 'primeng/card';
import { Tag } from 'primeng/tag';
import { SolicitudesService } from '../../servicios/solicitudes.service';
import { AuthService } from '../../servicios/auth.service';
import { SolicitudResumen } from '../../modelos/solicitudes';

@Component({
  selector: 'app-lista-solicitudes',
  imports: [RouterLink, SlicePipe, Card, Tag],
  templateUrl: './lista-solicitudes.html',
  styleUrl: './lista-solicitudes.css'
})
export class ListaSolicitudes implements OnInit {

  private solicitudesService = inject(SolicitudesService);
  private authService = inject(AuthService);
  private http = inject(HttpClient);

  esAdmin = this.authService.esAdmin();
  esAdministrativo = this.authService.esAdministrativo();

  solicitudes = signal<SolicitudResumen[]>([]);
  cargando = signal(true);

ngOnInit(): void {
  if (this.esAdmin || this.esAdministrativo) {
    this.solicitudesService.listar().subscribe({
      next: (data) => {
        this.solicitudes.set(data);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false)
    });
  } else {
    const email = this.authService.obtenerEmailDesdeToken();
    if (email) {
      this.http.get<any>(`http://localhost:8080/api/usuarios/buscar?email=${email}`)
        .subscribe({
          next: (usuario) => {
            this.solicitudesService.listarPorUsuario(usuario.id).subscribe({
              next: (data) => {
                this.solicitudes.set(data);
                this.cargando.set(false);
              },
              error: () => this.cargando.set(false)
            });
          },
          error: () => this.cargando.set(false)
        });
    }
  }
}

  tagSeveridad(estado: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast' {
    const mapa: Record<string, 'success' | 'info' | 'warn' | 'danger' | 'secondary' | 'contrast'> = {
      CLASIFICACION: 'secondary',
      PENDIENTE: 'warn',
      EN_PROCESO: 'info',
      ATENDIDA: 'success',
      CERRADA: 'contrast',
      CANCELADA: 'danger',
      RECHAZADA: 'danger'
    };
    return mapa[estado] ?? 'secondary';
  }

  tagPrioridad(prioridad: string): 'success' | 'warn' | 'danger' {
    const mapa: Record<string, 'success' | 'warn' | 'danger'> = {
      BAJA: 'success',
      MEDIA: 'warn',
      ALTA: 'danger'
    };
    return mapa[prioridad] ?? 'success';
  }

  mensajeEstado(estado: string): string {
    const mapa: Record<string, string> = {
      CLASIFICACION: 'Tu solicitud está siendo revisada por el equipo.',
      PENDIENTE: 'Tu solicitud fue clasificada y está en espera de atención.',
      EN_PROCESO: 'Un administrativo está atendiendo tu solicitud.',
      ATENDIDA: 'Tu solicitud fue atendida exitosamente.',
      CERRADA: 'Tu solicitud fue cerrada.',
      CANCELADA: 'Tu solicitud fue cancelada.',
      RECHAZADA: 'Tu solicitud fue rechazada.'
    };
    return mapa[estado] ?? '';
  }
}