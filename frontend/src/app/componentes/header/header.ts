import { Component, inject, signal, OnInit, effect } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './header.html',
  styleUrl: './header.css'
})
export class Header {

  private authService = inject(AuthService);
  private router = inject(Router);
  private http = inject(HttpClient);

  isLoggedIn = this.authService.isAuthenticated;
  nombreUsuario = signal('');

  get esAdminOAdministrativo(): boolean {
    return this.authService.esAdmin() || this.authService.esAdministrativo();
  }

  constructor() {
    effect(() => {
      if (this.authService.isAuthenticated()) {
        this.cargarNombreUsuario();
      } else {
        this.nombreUsuario.set('');
      }
    });
  }

  cargarNombreUsuario(): void {
    const email = this.authService.obtenerEmailDesdeToken();
    if (email) {
      this.http.get<any>(`http://localhost:8080/api/usuarios/buscar?email=${email}`)
        .subscribe({
          next: (usuario) => this.nombreUsuario.set(usuario.nombre),
          error: () => this.nombreUsuario.set(email)
        });
    }
  }

  cerrarSesion(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}