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

/**
 * Repositorio Spring Data JPA para la entidad {@link SolicitudEntity}.
 *
 * <p>Define las operaciones de acceso a datos usando los tres mecanismos
 * de Spring Data JPA: inferencia de métodos, JPQL y SQL nativo.
 * No es accedido directamente desde la capa de aplicación — actúa como
 * colaborador interno de {@link SolicitudJpaRepository}.</p>
 */
interface SolicitudJpaDataRepository extends JpaRepository<SolicitudEntity, Long> {

    // ── Inferencia de métodos ─────────────────────────────────────────────

    /** Busca una solicitud por su código único de negocio. */
    Optional<SolicitudEntity> findByCodigo(String codigo);

    /** Retorna todas las solicitudes con el estado indicado. */
    List<SolicitudEntity> findByEstado(EstadoSolicitudEnum estado);

    /** Retorna una página de solicitudes con el estado indicado. */
    Page<SolicitudEntity> findByEstado(EstadoSolicitudEnum estado, Pageable pageable);

    /** Retorna una página de solicitudes con el tipo indicado. */
    Page<SolicitudEntity> findByTipoSolicitud(TipoSolicitudEnum tipo, Pageable pageable);

    /** Retorna una página de solicitudes con la prioridad indicada. */
    Page<SolicitudEntity> findByPrioridad(PrioridadEnum prioridad, Pageable pageable);

    /** Retorna una página de solicitudes filtrando por estado y tipo simultáneamente. */
    Page<SolicitudEntity> findByEstadoAndTipoSolicitud(
            EstadoSolicitudEnum estado,
            TipoSolicitudEnum tipo,
            Pageable pageable
    );

    /** Busca solicitudes cuya descripción contenga el texto indicado (insensible a mayúsculas). */
    List<SolicitudEntity> findByDescripcionContainingIgnoreCase(String keyword);

    /** Verifica si existe una solicitud con el código indicado. */
    boolean existsByCodigo(String codigo);

    /** Cuenta el total de solicitudes con el estado indicado. */
    long countByEstado(EstadoSolicitudEnum estado);

    /** Retorna solicitudes del solicitante indicado. */
    List<SolicitudEntity> findBySolicitanteCodigo(String solicitanteCodigo);

    /** Retorna solicitudes en estado PENDIENTE sin responsable asignado. */
    List<SolicitudEntity> findByResponsableCodigoIsNullAndEstado(EstadoSolicitudEnum estado);

    /** Retorna solicitudes asignadas al responsable indicado. */
    List<SolicitudEntity> findByResponsableCodigo(String responsableCodigo);

    // ── JPQL ─────────────────────────────────────────────────────────────

    /**
     * Busca solicitudes cuyos estados estén dentro de la lista indicada.
     *
     * @param estados Lista de estados a incluir en el filtro.
     */
    @Query("SELECT s FROM SolicitudEntity s WHERE s.estado IN (:estados)")
    List<SolicitudEntity> buscarPorVariosEstados(
            @Param("estados") List<EstadoSolicitudEnum> estados
    );

    /**
     * Busca solicitudes aplicando filtros opcionales combinados.
     * Los parámetros nulos son ignorados automáticamente en la consulta.
     *
     * @param estado        Filtra por estado. Opcional.
     * @param tipo          Filtra por tipo de solicitud. Opcional.
     * @param prioridad     Filtra por prioridad. Opcional.
     * @param solicitanteId Filtra por código externo del solicitante. Opcional.
     * @param pageable      Configuración de paginación y ordenamiento.
     */
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

    /**
     * Busca una solicitud por código cargando su historial en la misma consulta.
     * Evita el problema N+1 que ocurriría al cargar el historial de forma perezosa.
     *
     * @param codigo Código único de la solicitud.
     */
    @Query("SELECT s FROM SolicitudEntity s LEFT JOIN FETCH s.historial WHERE s.codigo = :codigo")
    Optional<SolicitudEntity> buscarSolicitudConHistorial(@Param("codigo") String codigo);

    /**
     * Busca solicitudes que llevan más tiempo del límite indicado en estados activos.
     *
     * @param estados      Lista de estados activos a considerar (PENDIENTE, EN_PROCESO).
     * @param fechaLimite  Fecha a partir de la cual se consideran vencidas.
     */
    @Query("""
        SELECT s FROM SolicitudEntity s
        WHERE s.estado IN (:estados)
        AND s.fechaCreacion <= :fechaLimite
        """)
    List<SolicitudEntity> findVencidas(
            @Param("estados") List<EstadoSolicitudEnum> estados,
            @Param("fechaLimite") java.time.LocalDateTime fechaLimite
    );

    // ── SQL Nativo ────────────────────────────────────────────────────────

    /**
     * Agrupa el conteo de solicitudes por estado usando SQL nativo.
     * Retorna filas con [estado, total].
     */
    @Query(
            value = "SELECT estado, COUNT(*) as total FROM solicitudes GROUP BY estado",
            nativeQuery = true
    )
    List<Object[]> reporteAgrupacionPorEstado();

    /**
     * Agrupa el conteo de solicitudes por tipo usando SQL nativo.
     * Retorna filas con [tipo_solicitud, total].
     */
    @Query(
            value = "SELECT tipo_solicitud, COUNT(*) as total FROM solicitudes GROUP BY tipo_solicitud",
            nativeQuery = true
    )
    List<Object[]> reporteAgrupacionPorTipo();

    /**
     * Agrupa el conteo de solicitudes por responsable usando SQL nativo.
     * Solo incluye solicitudes con responsable asignado.
     * Retorna filas con [responsable_codigo, total].
     */
    @Query(
            value = "SELECT responsable_codigo, COUNT(*) as total FROM solicitudes WHERE responsable_codigo IS NOT NULL GROUP BY responsable_codigo",
            nativeQuery = true
    )
    List<Object[]> reporteAgrupacionPorResponsable();
}