package co.edu.uniquindio.proyecto.application.dto.response;

import java.util.Map;

/**
 * DTO de respuesta con estadísticas generales del sistema de solicitudes.
 *
 * <p>Consolida conteos por estado y por tipo, útil para paneles
 * administrativos que requieren una vista rápida del estado actual
 * del sistema.</p>
 *
 * @param totalSolicitudes Cantidad total de solicitudes registradas.
 * @param pendientes       Solicitudes clasificadas aún sin responsable asignado.
 * @param enProceso        Solicitudes actualmente en revisión por un responsable.
 * @param atendidas        Solicitudes marcadas como atendidas pendientes de cierre.
 * @param rechazadas       Solicitudes rechazadas durante la revisión.
 * @param cerradas         Solicitudes cerradas definitivamente.
 * @param canceladas       Solicitudes canceladas por el solicitante.
 * @param sinResponsable   Solicitudes pendientes sin responsable asignado.
 * @param porTipo          Conteo de solicitudes agrupadas por tipo de solicitud.
 */
public record DashboardResponse(
        long totalSolicitudes,
        long pendientes,
        long enProceso,
        long atendidas,
        long rechazadas,
        long cerradas,
        long canceladas,
        long sinResponsable,
        Map<String, Long> porTipo
) {}