package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una solicitud académica en la base de datos.
 *
 * <p>Es el espejo de persistencia del agregado raíz {@code Solicitud} del dominio.
 * No contiene lógica de negocio — su único propósito es mapear el estado
 * del agregado a las tablas relacionales {@code solicitudes} y
 * {@code eventos_historial}.</p>
 *
 * <p>El historial se persiste como una colección de embebidos
 * ({@link EventoHistorialEmbeddable}) ordenados por la columna
 * {@code secuencia} para garantizar el orden cronológico.</p>
 */
@Entity
@Table(name = "solicitudes", indexes = {
        @Index(name = "idx_solicitud_codigo", columnList = "codigo", unique = true),
        @Index(name = "idx_solicitud_estado", columnList = "estado"),
        @Index(name = "idx_solicitud_solicitante", columnList = "solicitante_codigo")
})
@Getter
@Setter
@NoArgsConstructor
public class SolicitudEntity {

    /** Identificador interno generado automáticamente por la base de datos. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código único de negocio de la solicitud (ej. SOL-1234567890). */
    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    /** Motivo de la solicitud registrado por el solicitante. */
    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    /** Fecha y hora de registro de la solicitud. */
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    /** Tipo asignado durante la clasificación. Puede ser nulo hasta ese momento. */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solicitud", length = 30)
    private TipoSolicitudEnum tipoSolicitud;

    /** Prioridad calculada automáticamente durante la clasificación. */
    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", length = 10)
    private PrioridadEnum prioridad;

    /** Estado actual dentro del ciclo de vida de la solicitud. */
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSolicitudEnum estado;

    /** Código externo del usuario solicitante. */
    @Column(name = "solicitante_codigo", nullable = false, length = 50)
    private String solicitanteCodigo;

    /** Código externo del administrativo responsable. Nulo hasta la asignación. */
    @Column(name = "responsable_codigo", length = 50)
    private String responsableCodigo;

    /**
     * Historial cronológico de eventos de la solicitud.
     * Se carga de forma perezosa y se ordena por la columna {@code secuencia}.
     */
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "eventos_historial",
            joinColumns = @JoinColumn(name = "solicitud_id")
    )
    @OrderColumn(name = "secuencia")
    private List<EventoHistorialEmbeddable> historial = new ArrayList<>();
}