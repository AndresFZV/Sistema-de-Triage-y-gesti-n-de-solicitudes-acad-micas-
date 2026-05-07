import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { UsuarioService } from '../../servicios/usuario.service';

@Component({
  selector: 'app-registro',
  imports: [FormsModule, RouterLink],
  templateUrl: './registro.html',
  styleUrl: './registro.css'
})
export class Registro {

  request = {
    nombre: '',
    email: '',
    tipoUsuario: 'ESTUDIANTE',
    password: ''
  };

  error: string = '';
  exito: string = '';
  cargando: boolean = false;

  constructor(private usuarioService: UsuarioService, private router: Router) {}

  registrar(): void {
    this.error = '';
    this.exito = '';
    this.cargando = true;

    this.usuarioService.crear(this.request).subscribe({
      next: () => {
        this.exito = 'Usuario creado exitosamente. Redirigiendo al login...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        if (err.status === 409) {
          this.error = 'Ya existe un usuario con ese email.';
        } else {
          this.error = 'Error al crear el usuario. Intenta de nuevo.';
        }
        this.cargando = false;
      }
    });
  }
}