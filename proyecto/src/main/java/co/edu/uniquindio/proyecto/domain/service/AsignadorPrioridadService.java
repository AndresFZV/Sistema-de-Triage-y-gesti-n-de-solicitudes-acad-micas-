package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Servicio de dominio que calcula automáticamente la prioridad de una solicitud.
 *
 * <p>La prioridad se determina con base en dos factores:</p>
 * <ol>
 *   <li><b>Prioridad base:</b> definida por el tipo de solicitud.</li>
 *   <li><b>Envejecimiento:</b> si la solicitud lleva mucho tiempo sin atenderse,
 *       su prioridad sube automáticamente para evitar inanición.</li>
 * </ol>
 *
 * <p>Reglas de prioridad base:</p>
 * <ul>
 *   <li>{@link TipoSolicitud#HOMOLOGACION} → {@link Prioridad#ALTA}</li>
 *   <li>{@link TipoSolicitud#SOLICITUD_CUPO} → {@link Prioridad#ALTA}</li>
 *   <li>{@link TipoSolicitud#CANCELACION} → {@link Prioridad#MEDIA}</li>
 *   <li>{@link TipoSolicitud#OTRO} → {@link Prioridad#BAJA}</li>
 * </ul>
 *
 * <p>Reglas de envejecimiento:</p>
 * <ul>
 *   <li>Más de 3 días sin atender → sube un nivel de prioridad.</li>
 *   <li>Más de 7 días sin atender → sube directamente a {@link Prioridad#ALTA}.</li>
 * </ul>
 */
public class AsignadorPrioridadService {

    /** Días a partir de los cuales la prioridad sube un nivel. */
    private static final int DIAS_SUBIR_UN_NIVEL = 3;

    /** Días a partir de los cuales la prioridad sube directamente a ALTA. */
    private static final int DIAS_PRIORIDAD_ALTA = 7;

    /**
     * Calcula la prioridad de una solicitud considerando su tipo y tiempo de espera.
     *
     * @param tipo          Tipo de la solicitud.
     * @param fechaCreacion Fecha en que fue registrada la solicitud.
     * @return La prioridad calculada para la solicitud.
     */
    public Prioridad asignar(TipoSolicitud tipo, LocalDateTime fechaCreacion) {
        Prioridad prioridadBase = calcularPrioridadBase(tipo);
        return aplicarEnvejecimiento(prioridadBase, fechaCreacion);
    }

    /**
     * Determina la prioridad base según el tipo de solicitud.
     *
     * @param tipo Tipo de la solicitud.
     * @return Prioridad base correspondiente al tipo.
     */
    private Prioridad calcularPrioridadBase(TipoSolicitud tipo) {
        return switch (tipo) {
            case HOMOLOGACION, SOLICITUD_CUPO -> Prioridad.ALTA;
            case CANCELACION -> Prioridad.MEDIA;
            case OTRO -> Prioridad.BAJA;
        };
    }

    /**
     * Aplica las reglas de envejecimiento sobre la prioridad base.
     * Si la solicitud lleva muchos días sin atenderse, su prioridad sube.
     *
     * @param prioridadBase Prioridad base calculada por tipo.
     * @param fechaCreacion Fecha de creación de la solicitud.
     * @return Prioridad ajustada por envejecimiento.
     */
    private Prioridad aplicarEnvejecimiento(Prioridad prioridadBase, LocalDateTime fechaCreacion) {
        long diasEspera = ChronoUnit.DAYS.between(fechaCreacion, LocalDateTime.now());

        if (diasEspera >= DIAS_PRIORIDAD_ALTA)
            return Prioridad.ALTA;

        if (diasEspera >= DIAS_SUBIR_UN_NIVEL)
            return subirNivel(prioridadBase);

        return prioridadBase;
    }

    /**
     * Sube la prioridad un nivel. Si ya es {@link Prioridad#ALTA}, se mantiene.
     *
     * @param prioridad Prioridad actual.
     * @return Prioridad un nivel superior.
     */
    private Prioridad subirNivel(Prioridad prioridad) {
        return switch (prioridad) {
            case BAJA -> Prioridad.MEDIA;
            case MEDIA -> Prioridad.ALTA;
            case ALTA -> Prioridad.ALTA;
        };
    }
}