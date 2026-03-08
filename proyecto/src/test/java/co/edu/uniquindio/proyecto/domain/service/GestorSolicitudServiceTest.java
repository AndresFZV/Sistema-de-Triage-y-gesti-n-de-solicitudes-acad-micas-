package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para {@link GestorSolicitudService}.
 * Valida las reglas de roles: quién puede ejecutar cada acción
 * sobre el ciclo de vida de una solicitud.
 */
class GestorSolicitudServiceTest {

    private GestorSolicitudService gestor;
    private Usuario estudiante;
    private Usuario administrativo;
    private Usuario docente;

    @BeforeEach
    void setUp() {
        gestor = new GestorSolicitudService();
        estudiante = new Usuario("U-001", "Juan Pérez",
                new Email("juan@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        administrativo = new Usuario("U-002", "Ana Gómez",
                new Email("ana@uniquindio.edu.co"), TipoUsuario.ADMINISTRATIVO);
        docente = new Usuario("U-003", "Carlos López",
                new Email("carlos@uniquindio.edu.co"), TipoUsuario.DOCENTE);
    }

    @Test
    /** Verifica que cualquier tipo de usuario puede registrar una solicitud. */
    void cualquierUsuarioPuedeRegistrar() {
        assertDoesNotThrow(() -> gestor.registrar("Homologación", estudiante));
        assertDoesNotThrow(() -> gestor.registrar("Homologación", administrativo));
        assertDoesNotThrow(() -> gestor.registrar("Homologación", docente));
    }

    @Test
    /** Verifica que un docente no puede clasificar una solicitud. */
    void soloAdministrativoPuedeClasificar() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        assertThrows(ReglaDominioException.class, () ->
                gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, docente)
        );
    }

    @Test
    /** Verifica que un administrativo puede clasificar correctamente. */
    void administrativoPuedeClasificar() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
    }

    @Test
    /** Verifica que un docente no puede poner en revisión una solicitud. */
    void soloAdministrativoPuedePonerEnRevision() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        assertThrows(ReglaDominioException.class, () ->
                gestor.enRevision(solicitud, docente)
        );
    }

    @Test
    /** Verifica que un administrativo puede poner en revisión correctamente. */
    void administrativoPuedePonerEnRevision() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        gestor.enRevision(solicitud, administrativo);
        assertEquals(EstadoSolicitud.EN_PROCESO, solicitud.getEstado());
    }

    @Test
    /** Verifica que un docente no puede marcar una solicitud como atendida. */
    void soloAdministrativoPuedeMarcarComoAtendida() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        gestor.enRevision(solicitud, administrativo);
        assertThrows(ReglaDominioException.class, () ->
                gestor.atendida(solicitud, docente)
        );
    }

    @Test
    /** Verifica que un docente no puede rechazar una solicitud. */
    void soloAdministrativoPuedeRechazar() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        gestor.enRevision(solicitud, administrativo);
        assertThrows(ReglaDominioException.class, () ->
                gestor.rechazar(solicitud, docente)
        );
    }

    @Test
    /** Verifica que un docente no puede cerrar una solicitud. */
    void soloAdministrativoPuedeCerrar() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        gestor.enRevision(solicitud, administrativo);
        gestor.atendida(solicitud, administrativo);
        assertThrows(ReglaDominioException.class, () ->
                gestor.cerrar(solicitud, docente)
        );
    }

    @Test
    /** Verifica que el solicitante puede cancelar su propia solicitud. */
    void solicitantePuedeCancelar() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        gestor.cancelar(solicitud, estudiante);
        assertEquals(EstadoSolicitud.CANCELADA, solicitud.getEstado());
    }

    @Test
    /** Verifica que otro usuario no puede cancelar una solicitud ajena. */
    void otroUsuarioNoPuedeCancelar() {
        Solicitud solicitud = gestor.registrar("Homologación", estudiante);
        gestor.clasificar(solicitud, TipoSolicitud.HOMOLOGACION, administrativo);
        assertThrows(ReglaDominioException.class, () ->
                gestor.cancelar(solicitud, administrativo)
        );
    }
}