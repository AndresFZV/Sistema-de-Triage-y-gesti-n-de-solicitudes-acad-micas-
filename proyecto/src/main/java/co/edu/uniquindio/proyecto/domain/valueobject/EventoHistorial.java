package co.edu.uniquindio.proyecto.domain.valueobject;

import java.time.LocalDateTime;

public record EventoHistorial(
        String descripcion,
        EstadoSolicitud estadoResultante,
        String realizadoPor,
        LocalDateTime fecha
) {
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

    public static EventoHistorial registrado(String solicitante) {
        return new EventoHistorial(
                "Solicitud registrada",
                EstadoSolicitud.CLASIFICACION,
                solicitante,
                LocalDateTime.now()
        );
    }

    public static EventoHistorial clasificado(String responsable, TipoSolicitud tipo, Prioridad prioridad) {
        return new EventoHistorial(
                "Solicitud clasificada como " + tipo + " con prioridad " + prioridad,
                EstadoSolicitud.PENDIENTE,
                responsable,
                LocalDateTime.now()
        );
    }

    public static EventoHistorial enRevision(String responsable) {
        return new EventoHistorial(
                "Solicitud puesta en revisión",
                EstadoSolicitud.EN_PROCESO,
                responsable,
                LocalDateTime.now()
        );
    }

    public static EventoHistorial atendido(String responsable) {
        return new EventoHistorial(
                "Solicitud marcada como atendida",
                EstadoSolicitud.ATENDIDA,
                responsable,
                LocalDateTime.now()
        );
    }

    public static EventoHistorial cerrado(String responsable) {
        return new EventoHistorial(
                "Solicitud cerrada",
                EstadoSolicitud.CERRADA,
                responsable,
                LocalDateTime.now()
        );
    }

    public static EventoHistorial rechazado(String responsable) {
        return new EventoHistorial(
                "Solicitud rechazada",
                EstadoSolicitud.RECHAZADA,
                responsable,
                LocalDateTime.now()
        );
    }

    public static EventoHistorial cancelado(String solicitante) {
        return new EventoHistorial(
                "Solicitud cancelada por el solicitante",
                EstadoSolicitud.CANCELADA,
                solicitante,
                LocalDateTime.now()
        );
    }
}