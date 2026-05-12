import { Component, DestroyRef, inject, signal, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CrearSolicitudRequest } from '../../modelos/solicitudes';
import { AuthService } from '../../servicios/auth.service';

@Component({
  selector: 'app-nueva-solicitud',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './nueva-solicitud.html',
  styleUrl: './nueva-solicitud.css'
})
export class NuevaSolicitud implements OnInit {

  private http = inject(HttpClient);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);
  private authService = inject(AuthService);

  result = signal('');
  isLoading = signal(false);
  solicitanteId = signal('');

  solicitudForm = inject(FormBuilder).group({
    descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
  });

  ngOnInit(): void {
    const email = this.authService.obtenerEmailDesdeToken();
    if (email) {
      this.http.get<any>(`http://localhost:8080/api/usuarios/buscar?email=${email}`)
        .subscribe({
          next: (usuario) => {
            this.solicitanteId.set(usuario.id);
          },
          error: () => {
            this.result.set('No se pudo obtener el ID del solicitante.');
          }
        });
    }
  }

  onSubmit(): void {
    if (this.solicitudForm.invalid || !this.solicitanteId()) return;

    this.isLoading.set(true);
    const solicitud: CrearSolicitudRequest = {
      descripcion: this.solicitudForm.value.descripcion!,
      solicitanteId: this.solicitanteId()
    };

    this.http.post<any>('http://localhost:8080/api/solicitudes', solicitud)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response) => {
          this.result.set(`Solicitud creada exitosamente: ${response.codigo}`);
          this.solicitudForm.reset();
          this.isLoading.set(false);
        },
        error: () => {
          this.result.set('Error al crear la solicitud. Verifica los datos.');
          this.isLoading.set(false);
        }
      });
  }
}