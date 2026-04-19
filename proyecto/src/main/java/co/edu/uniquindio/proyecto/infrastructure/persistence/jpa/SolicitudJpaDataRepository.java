package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.EstadoSolicitudEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.PrioridadEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.SolicitudEntity;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.TipoSolicitudEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface SolicitudJpaDataRepository extends JpaRepository<SolicitudEntity, Long> {

    // ---- Inferencia de métodos ----

    Optional<SolicitudEntity> findByCodigo(String codigo);

    List<SolicitudEntity> findByEstado(EstadoSolicitudEnum estado);

    Page<SolicitudEntity> findByEstado(EstadoSolicitudEnum estado, Pageable pageable);

    Page<SolicitudEntity> findByTipoSolicitud(TipoSolicitudEnum tipo, Pageable pageable);

    Page<SolicitudEntity> findByPrioridad(PrioridadEnum prioridad, Pageable pageable);

    Page<SolicitudEntity> findByEstadoAndTipoSolicitud(
            EstadoSolicitudEnum estado,
            TipoSolicitudEnum tipo,
            Pageable pageable
    );

    List<SolicitudEntity> findByDescripcionContainingIgnoreCase(String keyword);

    boolean existsByCodigo(String codigo);

    long countByEstado(EstadoSolicitudEnum estado);

    // ---- JPQL ----

    @Query("SELECT s FROM SolicitudEntity s WHERE s.estado IN (:estados)")
    List<SolicitudEntity> buscarPorVariosEstados(
            @Param("estados") List<EstadoSolicitudEnum> estados
    );

    @Query("""
            SELECT s FROM SolicitudEntity s
            WHERE (:estado IS NULL OR s.estado = :estado)
            AND (:tipo IS NULL OR s.tipoSolicitud = :tipo)
            AND (:prioridad IS NULL OR s.prioridad = :prioridad)
            AND (:solicitanteId IS NULL OR s.solicitanteCodigo = :solicitanteId)
            """)
    Page<SolicitudEntity> buscarConFiltros(
            @Param("estado") EstadoSolicitudEnum estado,
            @Param("tipo") TipoSolicitudEnum tipo,
            @Param("prioridad") PrioridadEnum prioridad,
            @Param("solicitanteId") String solicitanteId,
            Pageable pageable
    );

    @Query("SELECT s FROM SolicitudEntity s LEFT JOIN FETCH s.historial WHERE s.codigo = :codigo")
    Optional<SolicitudEntity> buscarSolicitudConHistorial(@Param("codigo") String codigo);

    @Query(
            value = "SELECT estado, COUNT(*) as total FROM solicitudes GROUP BY estado",
            nativeQuery = true
    )
    List<Object[]> reporteAgrupacionPorEstado();

    List<SolicitudEntity> findBySolicitanteCodigo(String solicitanteCodigo);

    // Solicitudes sin responsable asignado en estado PENDIENTE
    List<SolicitudEntity> findByResponsableCodigoIsNullAndEstado(EstadoSolicitudEnum estado);

    // Solicitudes por responsable
    List<SolicitudEntity> findByResponsableCodigo(String responsableCodigo);

    // Reporte por tipo
    @Query(value = "SELECT tipo_solicitud, COUNT(*) as total FROM solicitudes GROUP BY tipo_solicitud", nativeQuery = true)
    List<Object[]> reporteAgrupacionPorTipo();

    // Reporte por responsable
    @Query(value = "SELECT responsable_codigo, COUNT(*) as total FROM solicitudes WHERE responsable_codigo IS NOT NULL GROUP BY responsable_codigo", nativeQuery = true)
    List<Object[]> reporteAgrupacionPorResponsable();

    // Solicitudes vencidas - más de N días en estado PENDIENTE o EN_PROCESO
    @Query("""
        SELECT s FROM SolicitudEntity s
        WHERE s.estado IN (:estados)
        AND s.fechaCreacion <= :fechaLimite
        """)
    List<SolicitudEntity> findVencidas(
            @Param("estados") List<EstadoSolicitudEnum> estados,
            @Param("fechaLimite") java.time.LocalDateTime fechaLimite
    );
}