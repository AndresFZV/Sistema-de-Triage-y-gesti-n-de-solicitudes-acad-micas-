package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventoHistorialEmbeddable {

    @Column(name = "descripcion", nullable = false, length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_resultante", nullable = false, length = 20)
    private EstadoSolicitudEnum estadoResultante;

    @Column(name = "realizado_por", nullable = false, length = 150)
    private String realizadoPor;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}