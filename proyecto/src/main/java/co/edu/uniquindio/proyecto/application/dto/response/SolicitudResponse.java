package co.edu.uniquindio.proyecto.application.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de respuesta con la información completa de una solicitud académica.
 *
 * <p>Expone los datos relevantes del agregado {@code Solicitud} sin revelar
 * detalles internos del dominio. El responsable puede ser nulo si la
 * solicitud aún no ha sido asignada a un administrativo.</p>
 *
 * @param codigo        Código único de la solicitud (ej. SOL-1234567890).
 * @param descripcion   Motivo de la solicitud registrado por el solicitante.
 * @param estado        Estado actual dentro del ciclo de vida.
 * @param tipoSolicitud Tipo asignado durante la clasificación.
 * @param prioridad     Nivel de urgencia calculado automáticamente por el sistema.
 * @param fechaCreacion Fecha y hora en que fue registrada la solicitud.
 * @param solicitante   Usuario que registró la solicitud.
 * @param responsable   Administrativo asignado como responsable. Puede ser nulo.
 */
public record SolicitudResponse(
        String codigo,
        String descripcion,
        String estado,
        String tipoSolicitud,
        String prioridad,
        LocalDateTime fechaCreacion,
        UsuarioResponse solicitante,
        UsuarioResponse responsable
) {}