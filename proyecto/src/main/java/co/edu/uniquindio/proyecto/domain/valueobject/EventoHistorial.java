package co.edu.uniquindio.proyecto.domain.valueobject;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;

import java.time.LocalDateTime;

/**
 * Value Object que representa un evento inmutable ocurrido en el ciclo de vida
 * de una solicitud.
 *
 * <p>Actúa como registro de auditoría. Cada vez que el estado de una solicitud
 * cambia, el agregado raíz {@link Solicitud} registra un nuevo evento en el historial.</p>
 *
 * <p>Solo puede ser creado a través de sus métodos factory estáticos, garantizando
 * que cada evento tenga una descripción semántica clara del dominio.</p>
 *
 * <p>El historial de una solicitud usa {@code LinkedHashSet} para garantizar
 * orden cronológico y ausencia de duplicados.</p>
 *
 * @param descripcion      Descripción legible del evento ocurrido.
 * @param estadoResultante Estado al que transitó la solicitud tras el evento.
 * @param realizadoPor     Nombre del usuario que realizó la acción.
 * @param fecha            Fecha y hora exacta en que ocurrió el evento.
 */
public record EventoHistorial(
        String descripcion,
        EstadoSolicitud estadoResultante,
        String realizadoPor,
        LocalDateTime fecha
) {
    /**
     * Constructor compacto que valida todos los campos obligatorios.
     *
     * @throws IllegalArgumentException si algún campo es nulo o vacío.
     */
    public EventoHistorial {
        if (descripcion == null || descripcion.isBlank())
            throw new IllegalArgumentException("La descripción del evento no puede estar vacía");
        if (estadoResultante == null)
            throw new IllegalArgumentException("El estado resultante es obligatorio");
        if (realizadoPor == null || realizadoPor.isBlank())
            throw new IllegalArgumentException("El responsable del evento es obligatorio");
        if (fecha == null)
            throw new IllegalArgumentException("La fecha del evento es obligatoria");
    }

    /**
     * Crea el evento inicial cuando una solicitud es registrada.
     *
     * @param solicitante Nombre del usuario que registró la solicitud.
     * @return Evento con estado resultante {@link EstadoSolicitud#CLASIFICACION}.
     */
    public static EventoHistorial registrado(String solicitante) {
        return new EventoHistorial(
                "Solicitud registrada",
                EstadoSolicitud.CLASIFICACION,
                solicitante,
                LocalDateTime.now()
        );
    }

    /**
     * Crea el evento cuando una solicitud es clasificada por un administrativo.
     *
     * @param responsable Nombre del administrativo que clasificó.
     * @param tipo        Tipo asignado a la solicitud.
     * @param prioridad   Prioridad calculada por el sistema.
     * @return Evento con estado resultante {@link EstadoSolicitud#PENDIENTE}.
     */
    public static EventoHistorial clasificado(String responsable, TipoSolicitud tipo, Prioridad prioridad) {
        return new EventoHistorial(
                "Solicitud clasificada como " + tipo + " con prioridad " + prioridad,
                EstadoSolicitud.PENDIENTE,
                responsable,
                LocalDateTime.now()
        );
    }

    /**
     * Crea el evento cuando una solicitud es puesta en revisión.
     *
     * @param responsable Nombre del administrativo asignado como responsable.
     * @return Evento con estado resultante {@link EstadoSolicitud#EN_PROCESO}.
     */
    public static EventoHistorial enRevision(String responsable) {
        return new EventoHistorial(
                "Solicitud puesta en revisión",
                EstadoSolicitud.EN_PROCESO,
                responsable,
                LocalDateTime.now()
        );
    }

    /**
     * Crea el evento cuando una solicitud es marcada como atendida.
     *
     * @param responsable Nombre del administrativo que atendió la solicitud.
     * @return Evento con estado resultante {@link EstadoSolicitud#ATENDIDA}.
     */
    public static EventoHistorial atendido(String responsable) {
        return new EventoHistorial(
                "Solicitud marcada como atendida",
                EstadoSolicitud.ATENDIDA,
                responsable,
                LocalDateTime.now()
        );
    }

    /**
     * Crea el evento cuando una solicitud es cerrada definitivamente.
     *
     * @param responsable Nombre de quien cerró la solicitud.
     * @return Evento con estado resultante {@link EstadoSolicitud#CERRADA}.
     */
    public static EventoHistorial cerrado(String responsable) {
        return new EventoHistorial(
                "Solicitud cerrada",
                EstadoSolicitud.CERRADA,
                responsable,
                LocalDateTime.now()
        );
    }

    /**
     * Crea el evento cuando una solicitud es rechazada durante la revisión.
     *
     * @param responsable Nombre del administrativo que rechazó la solicitud.
     * @return Evento con estado resultante {@link EstadoSolicitud#RECHAZADA}.
     */
    public static EventoHistorial rechazado(String responsable) {
        return new EventoHistorial(
                "Solicitud rechazada",
                EstadoSolicitud.RECHAZADA,
                responsable,
                LocalDateTime.now()
        );
    }

    /**
     * Crea el evento cuando el solicitante cancela su propia solicitud.
     *
     * @param solicitante Nombre del solicitante que canceló.
     * @return Evento con estado resultante {@link EstadoSolicitud#CANCELADA}.
     */
    public static EventoHistorial cancelado(String solicitante) {
        return new EventoHistorial(
                "Solicitud cancelada por el solicitante",
                EstadoSolicitud.CANCELADA,
                solicitante,
                LocalDateTime.now()
        );
    }
}