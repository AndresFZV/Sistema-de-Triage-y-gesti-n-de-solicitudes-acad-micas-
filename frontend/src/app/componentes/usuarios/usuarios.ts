import { Component, inject, signal, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-usuarios',
  imports: [FormsModule],
  templateUrl: './usuarios.html',
  styleUrl: './usuarios.css'
})
export class Usuarios implements OnInit {

  private http = inject(HttpClient);
  private authService = inject(AuthService);

  usuarios = signal<any[]>([]);
  cargando = signal(true);
  mensaje = signal('');
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
    this.mensaje.set('');
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
    this.mensaje.set('');
  }

  cancelar(): void {
    this.mostrarFormulario.set(false);
    this.mensaje.set('');
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
          this.mensaje.set('Usuario actualizado correctamente.');
          this.mostrarFormulario.set(false);
          this.cargarUsuarios();
        },
        error: () => this.mensaje.set('Error al actualizar el usuario.')
      });
    } else {
      this.http.post<any>('http://localhost:8080/api/usuarios', this.formulario)
        .subscribe({
          next: () => {
            this.mensaje.set('Usuario creado correctamente.');
            this.mostrarFormulario.set(false);
            this.cargarUsuarios();
          },
          error: () => this.mensaje.set('Error al crear el usuario.')
        });
    }
  }

  eliminar(id: string): void {
    if (!confirm('¿Estás seguro de eliminar este usuario?')) return;
    this.http.delete(`http://localhost:8080/api/usuarios/${id}`).subscribe({
      next: () => {
        this.mensaje.set('Usuario eliminado correctamente.');
        this.cargarUsuarios();
      },
      error: () => this.mensaje.set('Error al eliminar el usuario.')
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