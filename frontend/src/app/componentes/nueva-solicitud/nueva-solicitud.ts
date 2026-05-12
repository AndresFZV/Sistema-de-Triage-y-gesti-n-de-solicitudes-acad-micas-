import { Component, DestroyRef, inject, signal, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { CrearSolicitudRequest } from '../../modelos/solicitudes';
import { AuthService } from '../../servicios/auth.service';
import { NotificacionService } from '../../servicios/notificacion.services';

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
  private notificacion = inject(NotificacionService);

  result = signal('');
  isLoading = signal(false);
  solicitanteId = signal('');
  analizandoIA = signal(false);
  tipoSugerido = signal('');
  validacionIA = signal('');

  solicitudForm = inject(FormBuilder).group({
    descripcion: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
  });

  ngOnInit(): void {
    const email = this.authService.obtenerEmailDesdeToken();
    if (email) {
      this.http.get<any>(`http://localhost:8080/api/usuarios/buscar?email=${email}`)
        .subscribe({
          next: (usuario) => this.solicitanteId.set(usuario.id),
          error: () => this.notificacion.error('No se pudo obtener el ID del solicitante.')
        });
    }
  }

  analizarConIA(): void {
    const descripcion = this.solicitudForm.get('descripcion')?.value;
    if (!descripcion || descripcion.length < 10) {
      this.notificacion.advertencia('Escribe una descripción más detallada antes de analizar.');
      return;
    }

    this.analizandoIA.set(true);
    this.tipoSugerido.set('');
    this.validacionIA.set('');

    // Sugerir tipo
    this.http.post<any>('http://localhost:8080/api/ia/sugerir-tipo', { descripcion })
      .subscribe({
        next: (res) => {
          this.tipoSugerido.set(res.tipoSugerido);
        },
        error: () => {}
      });

    // Validar descripción
    this.http.post<any>('http://localhost:8080/api/ia/validar-descripcion', { descripcion })
      .subscribe({
        next: (res) => {
          this.validacionIA.set(res.resultado);
          this.analizandoIA.set(false);
        },
        error: () => this.analizandoIA.set(false)
      });
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
          this.notificacion.exito(`Solicitud creada: ${response.codigo}`);
          this.solicitudForm.reset();
          this.tipoSugerido.set('');
          this.validacionIA.set('');
          this.isLoading.set(false);
          setTimeout(() => this.router.navigate(['/mis-solicitudes']), 1500);
        },
        error: () => {
          this.notificacion.error('Error al crear la solicitud.');
          this.isLoading.set(false);
        }
      });
  }
}