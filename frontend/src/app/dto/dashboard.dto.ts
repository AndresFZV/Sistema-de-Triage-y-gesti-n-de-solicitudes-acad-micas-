export interface DashboardResponse {
  totalSolicitudes: number;
  pendientes: number;
  enProceso: number;
  atendidas: number;
  rechazadas: number;
  cerradas: number;
  canceladas: number;
  sinResponsable: number;
  porTipo: { [key: string]: number };
}