package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "codigo", unique = true, nullable = false, length = 50)
    private String codigo;

    @Column(name = "descripcion", nullable = false, length = 1000)
    private String descripcion;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_solicitud", length = 30)
    private TipoSolicitudEnum tipoSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(name = "prioridad", length = 10)
    private PrioridadEnum prioridad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoSolicitudEnum estado;

    @Column(name = "solicitante_codigo", nullable = false, length = 50)
    private String solicitanteCodigo;

    @Column(name = "responsable_codigo", length = 50)
    private String responsableCodigo;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "eventos_historial",
            joinColumns = @JoinColumn(name = "solicitud_id")
    )
    @OrderColumn(name = "secuencia")
    private List<EventoHistorialEmbeddable> historial = new ArrayList<>();
}