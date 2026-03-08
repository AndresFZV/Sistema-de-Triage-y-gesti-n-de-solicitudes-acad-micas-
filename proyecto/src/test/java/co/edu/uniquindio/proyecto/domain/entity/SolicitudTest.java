package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el Agregado Raíz {@link Solicitud}.
 * Valida las transiciones de estado, las invariantes del agregado
 * y la integridad del historial auditable.
 */
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
    /** Verifica que una solicitud recién creada inicia en estado CLASIFICACION. */
    void debeCrearSolicitudEnEstadoClasificacion() {
        Solicitud solicitud = new Solicitud("Homologación de materia", estudiante);
        assertEquals(EstadoSolicitud.CLASIFICACION, solicitud.getEstado());
    }

    @Test
    /** Verifica que no se puede crear una solicitud sin descripción. */
    void noDebeCrearSolicitudSinDescripcion() {
        assertThrows(ReglaDominioException.class, () ->
                new Solicitud("", estudiante)
        );
    }

    @Test
    /** Verifica que no se puede crear una solicitud sin solicitante. */
    void noDebeCrearSolicitudSinSolicitante() {
        assertThrows(ReglaDominioException.class, () ->
                new Solicitud("Homologación", null)
        );
    }

    // ---- Clasificación ----

    @Test
    /** Verifica que clasificar correctamente pasa el estado a PENDIENTE. */
    void debeClasificarSolicitudCorrectamente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);

        assertEquals(EstadoSolicitud.PENDIENTE, solicitud.getEstado());
        assertEquals(TipoSolicitud.HOMOLOGACION, solicitud.getTipoSolicitud());
        assertEquals(Prioridad.ALTA, solicitud.getPrioridad());
    }

    @Test
    /** Verifica que no se puede clasificar una solicitud que no está en CLASIFICACION. */
    void noDebeClasificarSolicitudQueNoEstaEnClasificacion() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.clasificar(TipoSolicitud.CANCELACION, Prioridad.MEDIA)
        );
    }

    // ---- En Revisión ----

    @Test
    /** Verifica que poner en revisión asigna responsable y pasa a EN_PROCESO. */
    void debePasarAEnProcesoConResponsable() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);

        assertEquals(EstadoSolicitud.EN_PROCESO, solicitud.getEstado());
        assertEquals(administrativo, solicitud.getResponsable());
    }

    @Test
    /** Verifica que no se puede poner en revisión una solicitud que no está PENDIENTE. */
    void noDebePasarARevisionSiNoEstaPendiente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.enRevision(administrativo)
        );
    }

    // ---- Atendida ----

    @Test
    /** Verifica que marcar como atendida pasa el estado a ATENDIDA. */
    void debeMarcarComoAtendida() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);
        solicitud.atendida();

        assertEquals(EstadoSolicitud.ATENDIDA, solicitud.getEstado());
    }

    @Test
    /** Verifica que no se puede atender una solicitud que no está EN_PROCESO. */
    void noDebeAtenderSolicitudQueNoEstaEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);

        assertThrows(ReglaDominioException.class, solicitud::atendida);
    }

    // ---- Rechazo ----

    @Test
    /** Verifica que rechazar una solicitud EN_PROCESO pasa el estado a RECHAZADA. */
    void debeRechazarSolicitudEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);
        solicitud.rechazar();

        assertEquals(EstadoSolicitud.RECHAZADA, solicitud.getEstado());
    }

    @Test
    /** Verifica que no se puede rechazar una solicitud que no está EN_PROCESO. */
    void noDebeRechazarSolicitudQueNoEstaEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);

        assertThrows(ReglaDominioException.class, solicitud::rechazar);
    }

    // ---- Cierre ----

    @Test
    /** Verifica que cerrar una solicitud ATENDIDA pasa el estado a CERRADA. */
    void debeCerrarSolicitudAtendida() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);
        solicitud.atendida();
        solicitud.cerrar();

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }

    @Test
    /** Verifica que cerrar una solicitud RECHAZADA pasa el estado a CERRADA. */
    void debeCerrarSolicitudRechazada() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);
        solicitud.rechazar();
        solicitud.cerrar();

        assertEquals(EstadoSolicitud.CERRADA, solicitud.getEstado());
    }

    @Test
    /** Verifica que no se puede cerrar una solicitud que está EN_PROCESO. */
    void noDebeCerrarSolicitudEnProceso() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);

        assertThrows(ReglaDominioException.class, solicitud::cerrar);
    }

    // ---- Cancelación ----

    @Test
    /** Verifica que el solicitante puede cancelar su solicitud cuando está PENDIENTE. */
    void debeCancelarSolicitudPendientePorelSolicitante() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.cancelar(estudiante);

        assertEquals(EstadoSolicitud.CANCELADA, solicitud.getEstado());
    }

    @Test
    /** Verifica que otro usuario no puede cancelar una solicitud ajena. */
    void noDebeCancelarSolicitudOtroUsuario() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.cancelar(administrativo)
        );
    }

    @Test
    /** Verifica que no se puede cancelar una solicitud que no está PENDIENTE. */
    void noDebeCancelarSolicitudQueNoEstaPendiente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);

        assertThrows(ReglaDominioException.class, () ->
                solicitud.cancelar(estudiante)
        );
    }

    // ---- Historial ----

    @Test
    /** Verifica que al crear la solicitud se registra automáticamente el primer evento. */
    void debeRegistrarEventoAlCrear() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        assertEquals(1, solicitud.getHistorial().size());
        assertEquals(EstadoSolicitud.CLASIFICACION,
                solicitud.getHistorial().get(0).estadoResultante());
    }

    @Test
    /** Verifica que el historial crece con cada acción realizada sobre la solicitud. */
    void historiaDebeCrecerConCadaAccion() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);
        solicitud.enRevision(administrativo);
        solicitud.atendida();
        solicitud.cerrar();

        assertEquals(5, solicitud.getHistorial().size());
    }

    @Test
    /** Verifica que el historial retornado es inmutable y no puede ser modificado externamente. */
    void noDebePermitirModificarHistorialDirectamente() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);

        assertThrows(UnsupportedOperationException.class, () ->
                solicitud.getHistorial().add(
                        EventoHistorial.registrado("intruso")
                )
        );
    }

    @Test
    /** Verifica que el último evento del historial siempre refleja el estado actual. */
    void historiaDebeEstarSincronizadoConEstado() {
        Solicitud solicitud = new Solicitud("Homologación", estudiante);
        solicitud.clasificar(TipoSolicitud.HOMOLOGACION, Prioridad.ALTA);

        EstadoSolicitud ultimoEstado = solicitud.getHistorial()
                .get(solicitud.getHistorial().size() - 1)
                .estadoResultante();

        assertEquals(solicitud.getEstado(), ultimoEstado);
    }
}