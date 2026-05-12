import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SlicePipe } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Tag } from 'primeng/tag';
import { SolicitudesService } from '../../servicios/solicitudes.service';
import { AuthService } from '../../servicios/auth.service';
import { SolicitudResumen } from '../../modelos/solicitudes';

@Component({
  selector: 'app-lista-solicitudes',
  imports: [RouterLink, SlicePipe, Tag, FormsModule],
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
  solicitudesFiltradas = signal<SolicitudResumen[]>([]);
  cargando = signal(true);
  paginaActual = signal(0);
  tamañoPagina = 9;

  filtroEstado = '';
  filtroTipo = '';
  filtroPrioridad = '';
  filtroBusqueda = '';

  estados = ['CLASIFICACION', 'PENDIENTE', 'EN_PROCESO', 'ATENDIDA', 'RECHAZADA', 'CERRADA', 'CANCELADA'];
  tipos = ['HOMOLOGACION', 'CANCELACION', 'SOLICITUD_CUPO', 'OTRO'];
  prioridades = ['ALTA', 'MEDIA', 'BAJA'];

  get solicitudesPaginadas(): SolicitudResumen[] {
    const inicio = this.paginaActual() * this.tamañoPagina;
    return this.solicitudesFiltradas().slice(inicio, inicio + this.tamañoPagina);
  }

  get totalPaginas(): number {
    return Math.ceil(this.solicitudesFiltradas().length / this.tamañoPagina);
  }

  get paginas(): number[] {
    return Array.from({ length: this.totalPaginas }, (_, i) => i);
  }

  irAPagina(pagina: number): void {
    this.paginaActual.set(pagina);
  }

  paginaAnterior(): void {
    if (this.paginaActual() > 0) {
      this.paginaActual.set(this.paginaActual() - 1);
    }
  }

  paginaSiguiente(): void {
    if (this.paginaActual() < this.totalPaginas - 1) {
      this.paginaActual.set(this.paginaActual() + 1);
    }
  }

  ngOnInit(): void {
    if (this.esAdmin || this.esAdministrativo) {
      this.solicitudesService.listar().subscribe({
        next: (data) => {
          this.solicitudes.set(data);
          this.solicitudesFiltradas.set(data);
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
                  this.solicitudesFiltradas.set(data);
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

  aplicarFiltros(): void {
    this.paginaActual.set(0);
    let resultado = this.solicitudes();

    if (this.filtroEstado) {
      resultado = resultado.filter(s => s.estado === this.filtroEstado);
    }
    if (this.filtroTipo) {
      resultado = resultado.filter(s => s.tipoSolicitud === this.filtroTipo);
    }
    if (this.filtroPrioridad) {
      resultado = resultado.filter(s => s.prioridad === this.filtroPrioridad);
    }
    if (this.filtroBusqueda.trim()) {
      const busqueda = this.filtroBusqueda.trim().toLowerCase();
      resultado = resultado.filter(s =>
        s.codigo.toLowerCase().includes(busqueda) ||
        s.descripcion.toLowerCase().includes(busqueda) ||
        s.solicitante.nombre.toLowerCase().includes(busqueda)
      );
    }

    this.solicitudesFiltradas.set(resultado);
  }

  limpiarFiltros(): void {
    this.filtroEstado = '';
    this.filtroTipo = '';
    this.filtroPrioridad = '';
    this.filtroBusqueda = '';
    this.paginaActual.set(0);
    this.solicitudesFiltradas.set(this.solicitudes());
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