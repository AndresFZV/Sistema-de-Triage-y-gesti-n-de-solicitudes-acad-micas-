package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class AsignadorPrioridadServiceTest {

    private AsignadorPrioridadService asignador;

    @BeforeEach
    void setUp() {
        asignador = new AsignadorPrioridadService();
    }

    // ---- Prioridad base ----

    @Test
    void homologacionDebeSerPrioridadAlta() {
        Prioridad prioridad = asignador.asignar(TipoSolicitud.HOMOLOGACION, LocalDateTime.now());
        assertEquals(Prioridad.ALTA, prioridad);
    }

    @Test
    void solicitudCupoDebeSerPrioridadAlta() {
        Prioridad prioridad = asignador.asignar(TipoSolicitud.SOLICITUD_CUPO, LocalDateTime.now());
        assertEquals(Prioridad.ALTA, prioridad);
    }

    @Test
    void cancelacionDebeSerPrioridadMedia() {
        Prioridad prioridad = asignador.asignar(TipoSolicitud.CANCELACION, LocalDateTime.now());
        assertEquals(Prioridad.MEDIA, prioridad);
    }

    @Test
    void otroDebeSerPrioridadBaja() {
        Prioridad prioridad = asignador.asignar(TipoSolicitud.OTRO, LocalDateTime.now());
        assertEquals(Prioridad.BAJA, prioridad);
    }

    // ---- Envejecimiento ----

    @Test
    void solicitudConMasDe3DiasDebeSubirUnNivel() {
        LocalDateTime hace4Dias = LocalDateTime.now().minusDays(4);
        Prioridad prioridad = asignador.asignar(TipoSolicitud.CANCELACION, hace4Dias);
        assertEquals(Prioridad.ALTA, prioridad);
    }

    @Test
    void solicitudConMasDe3DiasDeOtroDebeSubirAMedia() {
        LocalDateTime hace4Dias = LocalDateTime.now().minusDays(4);
        Prioridad prioridad = asignador.asignar(TipoSolicitud.OTRO, hace4Dias);
        assertEquals(Prioridad.MEDIA, prioridad);
    }

    @Test
    void solicitudConMasDe7DiasDebeSerSiempreAlta() {
        LocalDateTime hace8Dias = LocalDateTime.now().minusDays(8);
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.OTRO, hace8Dias));
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.CANCELACION, hace8Dias));
        assertEquals(Prioridad.ALTA, asignador.asignar(TipoSolicitud.HOMOLOGACION, hace8Dias));
    }

    @Test
    void solicitudAltaNoCambiaConEnvejecimiento() {
        LocalDateTime hace4Dias = LocalDateTime.now().minusDays(4);
        Prioridad prioridad = asignador.asignar(TipoSolicitud.HOMOLOGACION, hace4Dias);
        assertEquals(Prioridad.ALTA, prioridad);
    }
}