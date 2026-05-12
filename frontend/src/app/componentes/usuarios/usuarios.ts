import { Component, inject, signal, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { NotificacionService } from '../../servicios/notificacion.services';
import { ConfirmationService } from 'primeng/api';

@Component({
  selector: 'app-usuarios',
  imports: [FormsModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {

  private http = inject(HttpClient);
  private notificacion = inject(NotificacionService);
  private confirmacion = inject(ConfirmationService);

  usuarios = signal<any[]>([]);
  cargando = signal(true);
  error = signal('');

  mostrarFormulario = signal(false);
  modoEdicion = signal(false);
  usuarioEditandoId = signal('');

  formulario = {
    nombre: '',
    email: '',
    tipoUsuario: 'ESTUDIANTE',
    password: ''
  };

  ngOnInit(): void {
    this.cargarUsuarios();
  }

  cargarUsuarios(): void {
    this.http.get<any[]>('http://localhost:8080/api/usuarios').subscribe({
      next: (data) => {
        this.usuarios.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('Error al cargar usuarios.');
        this.cargando.set(false);
      }
    });
  }

  abrirFormularioCrear(): void {
    this.formulario = { nombre: '', email: '', tipoUsuario: 'ESTUDIANTE', password: '' };
    this.modoEdicion.set(false);
    this.mostrarFormulario.set(true);
  }

  abrirFormularioEditar(usuario: any): void {
    this.formulario = {
      nombre: usuario.nombre,
      email: usuario.email,
      tipoUsuario: usuario.tipoUsuario,
      password: ''
    };
    this.usuarioEditandoId.set(usuario.id);
    this.modoEdicion.set(true);
    this.mostrarFormulario.set(true);
  }

  cancelar(): void {
    this.mostrarFormulario.set(false);
  }

  guardar(): void {
    if (this.modoEdicion()) {
      this.http.put<any>(
        `http://localhost:8080/api/usuarios/${this.usuarioEditandoId()}`,
        {
          nombre: this.formulario.nombre,
          email: this.formulario.email,
          tipoUsuario: this.formulario.tipoUsuario
        }
      ).subscribe({
        next: () => {
          this.notificacion.exito('Usuario actualizado correctamente.');
          this.mostrarFormulario.set(false);
          this.cargarUsuarios();
        },
        error: () => this.notificacion.error('Error al actualizar el usuario.')
      });
    } else {
      this.http.post<any>('http://localhost:8080/api/usuarios', this.formulario)
        .subscribe({
          next: () => {
            this.notificacion.exito('Usuario creado correctamente.');
            this.mostrarFormulario.set(false);
            this.cargarUsuarios();
          },
          error: () => this.notificacion.error('Error al crear el usuario.')
        });
    }
  }

  eliminar(id: string, nombre: string): void {
    this.confirmacion.confirm({
      message: `¿Estás seguro de eliminar al usuario <strong>${nombre}</strong>? Esta acción no se puede deshacer.`,
      header: 'Confirmar eliminación',
      icon: 'fa-solid fa-triangle-exclamation',
      acceptLabel: 'Sí, eliminar',
      rejectLabel: 'Cancelar',
      acceptButtonStyleClass: 'p-button-danger',
      accept: () => {
        this.http.delete(`http://localhost:8080/api/usuarios/${id}`).subscribe({
          next: () => {
            this.notificacion.exito('Usuario eliminado correctamente.');
            this.cargarUsuarios();
          },
          error: () => this.notificacion.error('Error al eliminar el usuario.')
        });
      }
    });
  }

  badgeTipo(tipo: string): string {
    const mapa: Record<string, string> = {
      ADMINISTRATIVO: 'bg-danger',
      DOCENTE: 'bg-primary',
      ESTUDIANTE: 'bg-success'
    };
    return mapa[tipo] ?? 'bg-secondary';
  }
}