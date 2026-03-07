package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SolicitudTest {

    private Usuario estudiante;
    private Usuario administrativo;

    @BeforeEach
    void setUp() {
        estudiante = new Usuario("U-001", "Juan Pérez",
                new Email("juan@uniquindio.edu.co"), TipoUsuario.ESTUDIANTE);
        administrativo = new Usuario("U-002", "Ana Gómez",
                new Email("ana@uniquindio.edu.co"), TipoUsuario.ADMINISTRATIVO);
    }

    // ---- Creación ----

    @Test
    void debeCrearSolicitudEnEstadoClasificacion() {
        Solicitud solicitud = new Solicitud("Homologación de materia", estudiante);
        assertEquals(EstadoSolicitud.CLASIFICACION, solicitud.getEstado());
    }

    @Test
    void noDebeCrearSolicitudSinDescripcion() {
        assertThrows(ReglaDominioException.class, () ->
                new Solicitud("", estudiante)
        );
    }

    @Test
    void noDebeCrearSolicitudSinSolicitante() {
        assertThrows(ReglaDominioException.class, () ->
                new Solicitud("Homologación", null)
        );
    }

    // ---- Clasificación ----

    @Test
    void debeClasificarSolicitudCorrectamente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipoSolicitud());
        assertEquals(Prioridad.MEDIA, solicitud.getPrioridad());
    }

    @Test
    void noDebeClasificarSolicitudQueNoEstaEnClasificacion() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.clasificar(TipoSolicitud.CANCELACION, Prioridad.ALTA)
        );
    }

    // ---- En Revisión ----

    @Test
    void debePasarAEnProcesoConResponsable() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.enRevision(administrativo);

        assertEquals(EstadoSolicitud.EN_PROCESO, solicitud.getEstado());
        assertEquals(administrativo, solicitud.getResponsable());
    }

    @Test
    void noDebePasarARevisionSiNoEstaPendiente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.enRevision(administrativo)
        );
    }

    // ---- Atendida ----

    @Test
    void debeMarcarComoAtendida() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.enRevision(administrativo);
        solicitud.atendida();

        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    @Test
    void noDebeAtenderSolicitudQueNoEstaEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);

        assertThrows(ReglaDominioException.class, solicitud::atendida);
    }

    // ---- Rechazo ----

    @Test
    void debeRechazarSolicitudEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.enRevision(administrativo);
        solicitud.rechazar();

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
    }

    @Test
    void noDebeRechazarSolicitudQueNoEstaEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);

        assertThrows(ReglaDominioException.class, solicitud::rechazar);
    }

    // ---- Cierre ----

    @Test
    void debeCerrarSolicitudAtendida() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.enRevision(administrativo);
        solicitud.atendida();
        solicitud.cerrar();

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }

    @Test
    void debeCerrarSolicitudRechazada() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.enRevision(administrativo);
        solicitud.rechazar();
        solicitud.cerrar();

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }

    @Test
    void noDebeCerrarSolicitudEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.enRevision(administrativo);

        assertThrows(ReglaDominioException.class, solicitud::cerrar);
    }

    // ---- Cancelación ----

    @Test
    void debeCancelarSolicitudPendientePorelSolicitante() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.cancelar(estudiante);

        assertEquals(EstadoSolicitud.CANCELADA, solicitud.getEstado());
    }

    @Test
    void noDebeCancelarSolicitudOtroUsuario() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.cancelar(administrativo)
        );
    }

    @Test
    void noDebeCancelarSolicitudQueNoEstaPendiente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.cancelar(estudiante)
        );
    }

    // ---- Historial ----

    @Test
    void debeRegistrarEventoAlCrear() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        assertEquals(1, solicitud.getHistorial().size());
        assertEquals(EstadoSolicitud.CLASIFICACION,
                solicitud.getHistorial().get(0).estadoResultante());
    }

    @Test
    void historiaDebeCrecerConCadaAccion() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);
        solicitud.enRevision(administrativo);
        solicitud.atendida();
        solicitud.cerrar();

        assertEquals(5, solicitud.getHistorial().size());
    }

    @Test
    void noDebePermitirModificarHistorialDirectamente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);

        assertThrows(UnsupportedOperationException.class, () ->
                solicitud.getHistorial().add(
                        EventoHistorial.registrado("intruso")
                )
        );
    }

    @Test
    void historiaDebeEstarSincronizadoConEstado() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.MEDIA);

        EstadoSolicitud ultimoEstado = solicitud.getHistorial()
                .get(solicitud.getHistorial().size() - 1)
                .estadoResultante();

        assertEquals(solicitud.getEstado(), ultimoEstado);
    }
}