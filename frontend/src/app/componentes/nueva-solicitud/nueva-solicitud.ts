import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CrearSolicitudRequest, TipoSolicitud } from '../../modelos/solicitudes';

@Component({
  selector: 'app-nueva-solicitud',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './nueva-solicitud.html',
  styleUrl: './nueva-solicitud.css'
})
export class NuevaSolicitud {

  private http = inject(HttpClient);
  private router = inject(Router);
  private destroyRef = inject(DestroyRef);

  result = signal('');
  isLoading = signal(false);

  tipos: TipoSolicitud[] = ['HOMOLOGACION', 'CANCELACION', 'SOLICITUD_CUPO', 'OTRO'];

  solicitudForm = inject(FormBuilder).group({
    descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]],
    solicitanteId: ['', Validators.required]
  });

  onSubmit(): void {
    if (this.solicitudForm.invalid) return;

    this.isLoading.set(true);
    const solicitud = this.solicitudForm.value as CrearSolicitudRequest;

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