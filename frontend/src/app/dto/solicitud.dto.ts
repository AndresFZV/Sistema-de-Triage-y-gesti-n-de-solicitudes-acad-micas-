import { UsuarioResponse } from './usuario.dto';

export interface SolicitudResponse {
  codigo: string;
  descripcion: string;
  estado: string;
  tipoSolicitud: string;
  prioridad: string;
  fechaCreacion: string;
  solicitante: UsuarioResponse;
  responsable: UsuarioResponse | null;
}

export interface CrearSolicitudRequest {
  descripcion: string;
  solicitanteId: string;
}

export interface PaginaResponse<T> {
  contenido: T[];
  paginaActual: number;
  tamañoPagina: number;
  totalElementos: number;
  totalPaginas: number;
  primera: boolean;
  ultima: boolean;
}