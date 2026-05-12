import { Injectable, inject } from '@angular/core';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class NotificacionService {

  private messageService = inject(MessageService);

  exito(mensaje: string, detalle?: string): void {
    this.messageService.add({
      severity: 'success',
      summary: detalle || 'Éxito',
      detail: mensaje,
      life: 3000
    });
  }

  error(mensaje: string, detalle?: string): void {
    this.messageService.add({
      severity: 'error',
      summary: detalle || 'Error',
      detail: mensaje,
      life: 4000
    });
  }

  info(mensaje: string, detalle?: string): void {
    this.messageService.add({
      severity: 'info',
      summary: detalle || 'Información',
      detail: mensaje,
      life: 3000
    });
  }

  advertencia(mensaje: string, detalle?: string): void {
    this.messageService.add({
      severity: 'warn',
      summary: detalle || 'Advertencia',
      detail: mensaje,
      life: 3000
    });
  }
}