export type TipoSolicitud =
  'HOMOLOGACION' | 'CANCELACION' | 'SOLICITUD_CUPO' | 'OTRO';

export type EstadoSolicitud =
  'CLASIFICACION' | 'PENDIENTE' | 'EN_PROCESO' | 'ATENDIDA' | 'CERRADA' | 'CANCELADA' | 'RECHAZADA';

export type Prioridad = 'BAJA' | 'MEDIA' | 'ALTA';

export interface SolicitudResumen {
  codigo: string;
  descripcion: string;
  estado: EstadoSolicitud;
  tipoSolicitud: TipoSolicitud;
  prioridad: Prioridad;
  fechaCreacion: string;
  solicitante: {
    id: string;
    nombre: string;
    email: string;
    tipoUsuario: string;
  };
}

export interface CrearSolicitudRequest {
  descripcion: string;
  solicitanteId: string;
}