import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { SlicePipe } from '@angular/common';
import { AuthService } from '../../servicios/auth.service';
import { FormsModule } from '@angular/forms';
import { NotificacionService } from '../../servicios/notificacion.services';

@Component({
  selector: 'app-solicitud-detalle',
  imports: [SlicePipe, FormsModule],
  templateUrl: './solicitud-detalle.html',
  styleUrl: './solicitud-detalle.css'
})
export class SolicitudDetalle implements OnInit {

  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthService);
  private notificacion = inject(NotificacionService);

  solicitud = signal<any>(null);
  historial = signal<any[]>([]);
  admins = signal<any[]>([]);
  cargando = signal(true);
  error = signal('');
  resumenIA = signal('');
  cargandoResumen = signal(false);

  esAdmin = this.authService.esAdmin();
  esAdministrativo = this.authService.esAdministrativo();
  adminId = signal('');
  responsableSeleccionado = '';
  tipoSolicitud = '';

  ngOnInit(): void {
    const codigo = this.route.snapshot.paramMap.get('codigo');
    if (codigo) {
      this.cargarSolicitud(codigo);
      this.cargarHistorial(codigo);
    }

    if (this.esAdmin || this.esAdministrativo) {
      const email = this.authService.obtenerEmailDesdeToken();
      if (email) {
        this.http.get<any>(`http://localhost:8080/api/usuarios/buscar?email=${email}`)
          .subscribe({
            next: (usuario) => {
              this.adminId.set(usuario.id);
              this.responsableSeleccionado = usuario.id;
            },
            error: () => {}
          });
      }

      this.http.get<any[]>('http://localhost:8080/api/usuarios').subscribe({
        next: (usuarios) => {
          const admins = usuarios.filter(u =>
            u.tipoUsuario === 'ADMINISTRATIVO' || u.tipoUsuario === 'DOCENTE'
          );
          this.admins.set(admins);
        },
        error: () => {}
      });
    }
  }

  cargarSolicitud(codigo: string): void {
    this.http.get<any>(`http://localhost:8080/api/solicitudes/${codigo}`)
      .subscribe({
        next: (data) => {
          this.solicitud.set(data);
          this.cargando.set(false);
        },
        error: () => {
          this.error.set('Error al cargar la solicitud.');
          this.cargando.set(false);
        }
      });
  }

  cargarHistorial(codigo: string): void {
    this.http.get<any[]>(`http://localhost:8080/api/solicitudes/${codigo}/historial`)
      .subscribe({
        next: (data) => this.historial.set(data),
        error: () => {}
      });
  }

  generarResumen(): void {
    const descripcion = this.solicitud()?.descripcion;
    if (!descripcion) return;

    this.cargandoResumen.set(true);
    this.http.post<any>('http://localhost:8080/api/ia/resumir', { descripcion })
      .subscribe({
        next: (res) => {
          this.resumenIA.set(res.resumen);
          this.cargandoResumen.set(false);
        },
        error: () => {
          this.notificacion.error('No se pudo generar el resumen.');
          this.cargandoResumen.set(false);
        }
      });
  }

  clasificar(): void {
    const codigo = this.solicitud()?.codigo;
    this.http.patch<any>(`http://localhost:8080/api/solicitudes/${codigo}/clasificar`, {
      tipoSolicitud: this.tipoSolicitud,
      adminId: this.adminId()
    }).subscribe({
      next: (data) => {
        this.solicitud.set(data);
        this.notificacion.exito('Solicitud clasificada correctamente.');
        this.cargarHistorial(codigo);
      },
      error: () => this.notificacion.error('Error al clasificar la solicitud.')
    });
  }

  enRevision(): void {
    const codigo = this.solicitud()?.codigo;
    this.http.patch<any>(`http://localhost:8080/api/solicitudes/${codigo}/revision`, {
      responsableId: this.responsableSeleccionado
    }).subscribe({
      next: (data) => {
        this.solicitud.set(data);
        this.notificacion.exito('Solicitud puesta en revisión.');
        this.cargarHistorial(codigo);
      },
      error: () => this.notificacion.error('Error al poner en revisión.')
    });
  }

  atender(): void {
    const codigo = this.solicitud()?.codigo;
    this.http.patch<any>(`http://localhost:8080/api/solicitudes/${codigo}/atender`, {
      adminId: this.adminId()
    }).subscribe({
      next: (data) => {
        this.solicitud.set(data);
        this.notificacion.exito('Solicitud atendida correctamente.');
        this.cargarHistorial(codigo);
      },
      error: () => this.notificacion.error('Error al atender la solicitud.')
    });
  }

  rechazar(): void {
    const codigo = this.solicitud()?.codigo;
    this.http.patch<any>(`http://localhost:8080/api/solicitudes/${codigo}/rechazar`, {
      adminId: this.adminId()
    }).subscribe({
      next: (data) => {
        this.solicitud.set(data);
        this.notificacion.advertencia('Solicitud rechazada.');
        this.cargarHistorial(codigo);
      },
      error: () => this.notificacion.error('Error al rechazar la solicitud.')
    });
  }

  cerrar(): void {
    const codigo = this.solicitud()?.codigo;
    this.http.patch<any>(`http://localhost:8080/api/solicitudes/${codigo}/cerrar`, {
      adminId: this.adminId()
    }).subscribe({
      next: (data) => {
        this.solicitud.set(data);
        this.notificacion.info('Solicitud cerrada.');
        this.cargarHistorial(codigo);
      },
      error: () => this.notificacion.error('Error al cerrar la solicitud.')
    });
  }

  cancelar(): void {
    const codigo = this.solicitud()?.codigo;
    const solicitanteId = this.solicitud()?.solicitante?.id;
    this.http.patch<any>(`http://localhost:8080/api/solicitudes/${codigo}/cancelar`, {
      solicitanteId
    }).subscribe({
      next: (data) => {
        this.solicitud.set(data);
        this.notificacion.advertencia('Solicitud cancelada.');
        this.cargarHistorial(codigo);
      },
      error: () => this.notificacion.error('Error al cancelar la solicitud.')
    });
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

  volver(): void {
    if (this.esAdmin || this.esAdministrativo) {
      this.router.navigate(['/lista-solicitudes']);
    } else {
      this.router.navigate(['/mis-solicitudes']);
    }
  }
}