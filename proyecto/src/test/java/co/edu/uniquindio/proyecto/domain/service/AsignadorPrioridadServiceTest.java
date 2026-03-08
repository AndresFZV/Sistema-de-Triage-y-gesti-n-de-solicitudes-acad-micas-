package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link AsignadorPrioridadService}.
 * Valida las reglas de prioridad base por tipo y las reglas
 * de envejecimiento por tiempo de espera.
 */
class AsignadorPrioridadServiceTest {

    private AsignadorPrioridadService asignador;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorPrioridadService();
    }

    // ---- Prioridad base ----

    @Test
    /** Verifica que HOMOLOGACION tiene prioridad base ALTA. */
    void homologacionDebeSerPrioridadAlta() {
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.HOMOLOGACION, LocalDateTime.now()));
    }

    @Test
    /** Verifica que SOLICITUD_CUPO tiene prioridad base ALTA. */
    void solicitudCupoDebeSerPrioridadAlta() {
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.SOLICITUD_CUPO, LocalDateTime.now()));
    }

    @Test
    /** Verifica que CANCELACION tiene prioridad base MEDIA. */
    void cancelacionDebeSerPrioridadMedia() {
        assertEquals(Prioridad.MEDIA, asignador.asignar(TipoSolicitud.CANCELACION, LocalDateTime.now()));
    }

    @Test
    /** Verifica que OTRO tiene prioridad base BAJA. */
    void otroDebeSerPrioridadBaja() {
        assertEquals(Prioridad.BAJA, asignador.asignar(TipoSolicitud.OTRO, LocalDateTime.now()));
    }

    // ---- Envejecimiento ----

    @Test
    /** Verifica que CANCELACION con más de 3 días sube de MEDIA a ALTA. */
    void solicitudConMasDe3DiasDebeSubirUnNivel() {
        LocalDateTime hace4Dias = LocalDateTime.now().minusDays(4);
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.CANCELACION, hace4Dias));
    }

    @Test
    /** Verifica que OTRO con más de 3 días sube de BAJA a MEDIA. */
    void solicitudConMasDe3DiasDeOtroDebeSubirAMedia() {
        LocalDateTime hace4Dias = LocalDateTime.now().minusDays(4);
        assertEquals(Prioridad.MEDIA, asignador.asignar(TipoSolicitud.OTRO, hace4Dias));
    }

    @Test
    /** Verifica que cualquier tipo con más de 7 días pasa directamente a ALTA. */
    void solicitudConMasDe7DiasDebeSerSiempreAlta() {
        LocalDateTime hace8Dias = LocalDateTime.now().minusDays(8);
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.OTRO, hace8Dias));
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.CANCELACION, hace8Dias));
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.HOMOLOGACION, hace8Dias));
    }

    @Test
    /** Verifica que una prioridad ALTA no cambia con el envejecimiento. */
    void solicitudAltaNoCambiaConEnvejecimiento() {
        LocalDateTime hace4Dias = LocalDateTime.now().minusDays(4);
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.HOMOLOGACION, hace4Dias));
    }
}