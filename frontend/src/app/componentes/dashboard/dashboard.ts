import { Component, inject, signal, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dashboard',
  imports: [RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  private http = inject(HttpClient);
  private router = inject(Router);

  objectEntries(obj: any): [string, any][] {
  return obj ? Object.entries(obj) : [];
}

  dashboard = signal<any>(null);
  cargando = signal(true);
  error = signal('');

  roles: string[] = JSON.parse(localStorage.getItem('roles') || '[]');
  esAdmin = this.roles.includes('ADMIN');

  ngOnInit(): void {
    this.http.get<any>('http://localhost:8080/api/solicitudes/dashboard')
      .subscribe({
        next: (data) => {
          this.dashboard.set(data);
          this.cargando.set(false);
        },
        error: () => {
          this.error.set('Error al cargar el dashboard.');
          this.cargando.set(false);
        }
      });
  }

  cerrarSesion(): void {
    localStorage.clear();
    this.router.navigate(['/login']);
  }
}