import { Component, inject, signal, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../servicios/auth.service';
import { NotificacionService } from '../../servicios/notificacion.services';

@Component({
  selector: 'app-perfil',
  imports: [FormsModule],
  templateUrl: './perfil.html',
  styleUrl: './perfil.css'
})
export class Perfil implements OnInit {

  private http = inject(HttpClient);
  private authService = inject(AuthService);
  private notificacion = inject(NotificacionService);

  usuario = signal<any>(null);
  cargando = signal(true);
  editando = signal(false);

  formulario = {
    nombre: '',
    email: '',
    tipoUsuario: ''
  };

  ngOnInit(): void {
    const email = this.authService.obtenerEmailDesdeToken();
    if (email) {
      this.http.get<any>(`http://localhost:8080/api/usuarios/buscar?email=${email}`)
        .subscribe({
          next: (data) => {
            this.usuario.set(data);
            this.formulario = {
              nombre: data.nombre,
              email: data.email,
              tipoUsuario: data.tipoUsuario
            };
            this.cargando.set(false);
          },
          error: () => this.cargando.set(false)
        });
    }
  }

  guardar(): void {
    const id = this.usuario()?.id;
    this.http.put<any>(`http://localhost:8080/api/usuarios/${id}`, this.formulario)
      .subscribe({
        next: (data) => {
          this.usuario.set(data);
          this.editando.set(false);
          this.notificacion.exito('Perfil actualizado correctamente.');
        },
        error: () => this.notificacion.error('Error al actualizar el perfil.')
      });
  }

  cancelarEdicion(): void {
    this.formulario = {
      nombre: this.usuario().nombre,
      email: this.usuario().email,
      tipoUsuario: this.usuario().tipoUsuario
    };
    this.editando.set(false);
  }
}