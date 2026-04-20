package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Objeto embebido que representa un evento del historial de una solicitud
 * en la base de datos.
 *
 * <p>Se persiste en la tabla {@code eventos_historial} como una colección
 * de elementos asociada a {@link SolicitudEntity}. Cada instancia es
 * inmutable en el dominio pero mutable en persistencia para permitir
 * que JPA la gestione correctamente.</p>
 *
 * <p>Es el espejo de {@code EventoHistorial} del dominio adaptado a
 * la capa de infraestructura.</p>
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoHistorialEmbeddable {

    /** Descripción legible del evento ocurrido. */
    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    /** Estado al que transitó la solicitud tras el evento. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_resultante", nullable = false, length = 20)
    private EstadoSolicitudEnum estadoResultante;

    /** Nombre del usuario que ejecutó la acción. */
    @Column(name = "realizado_por", nullable = false, length = 150)
    private String realizadoPor;

    /** Fecha y hora exacta en que ocurrió el evento. */
    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}