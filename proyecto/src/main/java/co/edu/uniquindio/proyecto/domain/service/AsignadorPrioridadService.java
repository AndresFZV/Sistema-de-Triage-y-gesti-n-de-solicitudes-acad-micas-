package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class AsignadorPrioridadService {

    private static final int DIAS_SUBIR_UN_NIVEL = 3;
    private static final int DIAS_PRIORIDAD_ALTA = 7;

    public Prioridad asignar(TipoSolicitud tipo, LocalDateTime fechaCreacion) {
        Prioridad prioridadBase = calcularPrioridadBase(tipo);
        return aplicarEnvejecimiento(prioridadBase, fechaCreacion);
    }

    private Prioridad calcularPrioridadBase(TipoSolicitud tipo) {
        return switch (tipo) {
            case HOMOLOGACION, SOLICITUD_CUPO -> Prioridad.ALTA;
            case CANCELACION -> Prioridad.MEDIA;
            case OTRO -> Prioridad.BAJA;
        };
    }

    private Prioridad aplicarEnvejecimiento(Prioridad prioridadBase, LocalDateTime fechaCreacion) {
        long diasEspera = ChronoUnit.DAYS.between(fechaCreacion, LocalDateTime.now());

        if (diasEspera >= DIAS_PRIORIDAD_ALTA)
            return Prioridad.ALTA;

        if (diasEspera >= DIAS_SUBIR_UN_NIVEL)
            return subirNivel(prioridadBase);

        return prioridadBase;
    }

    private Prioridad subirNivel(Prioridad prioridad) {
        return switch (prioridad) {
            case BAJA -> Prioridad.MEDIA;
            case MEDIA -> Prioridad.ALTA;
            case ALTA -> Prioridad.ALTA;
        };
    }
}