import { Component, inject, OnInit, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { SlicePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { SolicitudesService } from '../../servicios/solicitudes.service';
import { AuthService } from '../../servicios/auth.service';
import { SolicitudResumen } from '../../modelos/solicitudes';

@Component({
  selector: 'app-lista-solicitudes',
  imports: [RouterLink, SlicePipe],
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

  badgeEstado(estado: string): string {
    const mapa: Record<string, string> = {
      CLASIFICACION: 'bg-secondary',
      PENDIENTE: 'bg-warning text-dark',
      EN_PROCESO: 'bg-primary',
      ATENDIDA: 'bg-success',
      CERRADA: 'bg-dark',
      CANCELADA: 'bg-danger',
      RECHAZADA: 'bg-danger'
    };
    return mapa[estado] ?? 'bg-secondary';
  }

  badgePrioridad(prioridad: string): string {
    const mapa: Record<string, string> = {
      BAJA: 'bg-success',
      MEDIA: 'bg-warning text-dark',
      ALTA: 'bg-danger'
    };
    return mapa[prioridad] ?? 'bg-secondary';
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