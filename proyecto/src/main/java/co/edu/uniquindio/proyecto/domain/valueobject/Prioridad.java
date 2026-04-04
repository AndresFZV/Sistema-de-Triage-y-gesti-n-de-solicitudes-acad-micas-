package co.edu.uniquindio.proyecto.domain.valueobject;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Value Object que representa el nivel de urgencia de una solicitud.
 *
 * <p>Se calcula automáticamente con base en dos factores:</p>
 * <ol>
 *   <li><b>Prioridad base:</b> definida por el {@link TipoSolicitud}.</li>
 *   <li><b>Envejecimiento:</b> si la solicitud lleva mucho tiempo sin atenderse,
 *       su prioridad sube automáticamente para evitar inanición.</li>
 * </ol>
 *
 * <p>Reglas de prioridad base:</p>
 * <ul>
 *   <li>{@link TipoSolicitud#HOMOLOGACION} → {@link #ALTA}</li>
 *   <li>{@link TipoSolicitud#SOLICITUD_CUPO} → {@link #ALTA}</li>
 *   <li>{@link TipoSolicitud#CANCELACION} → {@link #MEDIA}</li>
 *   <li>{@link TipoSolicitud#OTRO} → {@link #BAJA}</li>
 * </ul>
 *
 * <p>Reglas de envejecimiento:</p>
 * <ul>
 *   <li>Más de 3 días sin atender → sube un nivel de prioridad.</li>
 *   <li>Más de 7 días sin atender → sube directamente a {@link #ALTA}.</li>
 * </ul>
 */
public enum Prioridad {

    /** Máxima urgencia. Requiere atención inmediata. */
    ALTA,

    /** Urgencia moderada. Debe atenderse en un plazo razonable. */
    MEDIA,

    /** Urgencia baja. Sin restricción de tiempo inmediata. */
    BAJA;

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
    public static Prioridad calcular(TipoSolicitud tipo, LocalDateTime fechaCreacion) {
        Prioridad base = calcularBase(tipo);
        return aplicarEnvejecimiento(base, fechaCreacion);
    }

    /**
     * Determina la prioridad base según el tipo de solicitud.
     *
     * @param tipo Tipo de la solicitud.
     * @return Prioridad base correspondiente al tipo.
     */
    private static Prioridad calcularBase(TipoSolicitud tipo) {
        return switch (tipo) {
            case HOMOLOGACION, SOLICITUD_CUPO -> ALTA;
            case CANCELACION -> MEDIA;
            case OTRO -> BAJA;
        };
    }

    /**
     * Aplica las reglas de envejecimiento sobre la prioridad base.
     * Si la solicitud lleva muchos días sin atenderse, su prioridad sube.
     *
     * @param base          Prioridad base calculada por tipo.
     * @param fechaCreacion Fecha de creación de la solicitud.
     * @return Prioridad ajustada por envejecimiento.
     */
    private static Prioridad aplicarEnvejecimiento(Prioridad base, LocalDateTime fechaCreacion) {
        long dias = ChronoUnit.DAYS.between(fechaCreacion, LocalDateTime.now());
        if (dias >= DIAS_PRIORIDAD_ALTA) return ALTA;
        if (dias >= DIAS_SUBIR_UN_NIVEL) return base.subirNivel();
        return base;
    }

    /**
     * Sube la prioridad un nivel. Si ya es {@link #ALTA}, se mantiene.
     *
     * @return Prioridad un nivel superior.
     */
    private Prioridad subirNivel() {
        return switch (this) {
            case BAJA -> MEDIA;
            case MEDIA, ALTA -> ALTA;
        };
    }
}