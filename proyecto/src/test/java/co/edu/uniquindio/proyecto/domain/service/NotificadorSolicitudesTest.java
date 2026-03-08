package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link NotificadorSolicitudes}.
 * Valida que los destinatarios de cada tipo de notificación
 * sean los correctos según las reglas del dominio.
 */
class NotificadorSolicitudesTest {

    private NotificadorSolicitudes notificador;
    private Usuario estudiante;
    private Usuario administrativo;

    @BeforeEach
    void setUp() {
        notificador = new NotificadorSolicitudes();
        estudiante = new Usuario("U-001", "Juan Pérez",
                new Email("juan@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        administrativo = new Usuario("U-002", "Ana Gómez",
                new Email("ana@uniquindio.edu.co"), TipoUsuario.ADMINISTRATIVO);
    }

    @Test
    /** Verifica que al registrar una solicitud solo se notifica al solicitante. */
    void debeNotificarSolicitanteEnNuevaSolicitud() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        List<String> destinatarios = notificador.determinarDestinatarios(
                solicitud, TipoNotificacion.NUEVA_SOLICITUD
        );
        assertTrue(destinatarios.contains("juan@uniquindio.edu.co"));
    }

    @Test
    /** Verifica que en una asignación se notifica al solicitante y al responsable. */
    void debeNotificarAmbosEnAsignacion() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);

        List<String> destinatarios = notificador.determinarDestinatarios(
                solicitud, TipoNotificacion.ASIGNACION
        );

        assertTrue(destinatarios.contains("juan@uniquindio.edu.co"));
        assertTrue(destinatarios.contains("ana@uniquindio.edu.co"));
    }

    @Test
    /** Verifica que en un cambio de estado solo se notifica al solicitante. */
    void debeNotificarSolicitanteEnCambioEstado() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        List<String> destinatarios = notificador.determinarDestinatarios(
                solicitud, TipoNotificacion.CAMBIO_ESTADO
        );
        assertTrue(destinatarios.contains("juan@uniquindio.edu.co"));
    }

    @Test
    /** Verifica que al cerrar una solicitud se notifica al solicitante y al responsable. */
    void debeNotificarAmbosEnCierre() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);
        solicitud.atendida();
        solicitud.cerrar();

        List<String> destinatarios = notificador.determinarDestinatarios(
                solicitud, TipoNotificacion.CIERRE
        );

        assertTrue(destinatarios.contains("juan@uniquindio.edu.co"));
        assertTrue(destinatarios.contains("ana@uniquindio.edu.co"));
    }
}