package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SolicitudJpaDataRepository extends JpaRepository<SolicitudEntity, Long> {

    // ── Inferencia básica ──────────────────────────────────────────────────

    Optional<SolicitudEntity> findByCodigo(String codigo);

    List<SolicitudEntity> findByEstado(EstadoSolicitudEnum estado);

    // Ejercicio 1 — Inferencia: tipo ordenado por prioridad desc y fecha desc
    // Prioridad es un enum simple (no embebido), por eso no se usa _nivel
    List<SolicitudEntity> findByTipoSolicitudOrderByPrioridadDescFechaCreacionDesc(TipoSolicitudEnum tipo);

    // ── JPQL ───────────────────────────────────────────────────────────────

    // Ejercicio 2 — JPQL equivalente al método de inferencia anterior
    @Query("""
        SELECT s FROM SolicitudEntity s
        WHERE s.tipoSolicitud = :tipo
        ORDER BY s.prioridad DESC, s.fechaCreacion DESC
    """)
    List<SolicitudEntity> buscarPorTipoOrdenado(@Param("tipo") TipoSolicitudEnum tipo);

    // Ejercicio 3 — JPQL: filtro opcional por código o solicitante
    // Si se pasa código tiene prioridad; si es null, filtra por solicitanteCodigo
    @Query("""
        SELECT s FROM SolicitudEntity s
        WHERE (:codigo IS NULL OR s.codigo = :codigo)
        AND (:solicitanteCodigo IS NULL OR s.solicitanteCodigo = :solicitanteCodigo)
    """)
    List<SolicitudEntity> buscarPorCodigoOSolicitante(
            @Param("codigo") String codigo,
            @Param("solicitanteCodigo") String solicitanteCodigo
    );

    // Ejercicio 4 — Paginación: solicitudes con estado distinto al indicado
    Page<SolicitudEntity> findByEstadoNot(EstadoSolicitudEnum estado, Pageable pageable);

}