import { Component, inject, signal, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { UIChart } from 'primeng/chart';

@Component({
  selector: 'app-dashboard',
  imports: [UIChart],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {

  private http = inject(HttpClient);

  cargando = signal(true);
  stats = signal<any>(null);

  dataEstados: any;
  dataTipos: any;
  opciones: any;

  ngOnInit(): void {
    this.http.get<any>('http://localhost:8080/api/solicitudes/dashboard')
      .subscribe({
        next: (data) => {
          this.stats.set(data);
          this.construirGraficas(data);
          this.cargando.set(false);
        },
        error: () => this.cargando.set(false)
      });
  }

  construirGraficas(data: any): void {
    this.dataEstados = {
      labels: ['Pendientes', 'En Proceso', 'Atendidas', 'Rechazadas', 'Cerradas', 'Canceladas'],
      datasets: [{
        data: [
          data.pendientes ?? 0,
          data.enProceso ?? 0,
          data.atendidas ?? 0,
          data.rechazadas ?? 0,
          data.cerradas ?? 0,
          data.canceladas ?? 0
        ],
        backgroundColor: [
          '#ffc107', '#0d6efd', '#198754',
          '#dc3545', '#212529', '#fd7e14'
        ]
      }]
    };

    const tiposEntries = Object.entries(data.porTipo || {});
    this.dataTipos = {
      labels: tiposEntries.map(([k]) => k),
      datasets: [{
        label: 'Solicitudes por tipo',
        data: tiposEntries.map(([, v]) => v),
        backgroundColor: '#0d6efd',
        borderColor: '#0a58ca',
        borderWidth: 1
      }]
    };

    this.opciones = {
      responsive: true,
      plugins: {
        legend: { position: 'bottom' }
      }
    };
  }

  pendientesSinAsignar(): number {
    return this.stats()?.sinResponsable ?? 0;
  }
}