package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.service.AsignadorPrioridadService;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Agregado Raíz del dominio. Representa una solicitud académica formal
 * registrada por cualquier usuario del sistema.
 *
 * <p>Como agregado raíz, {@code Solicitud} es el único punto de entrada
 * para modificar su estado y su historial. Ninguna clase externa puede
 * alterar directamente sus invariantes.</p>
 *
 * <p>Invariantes que protege:</p>
 * <ul>
 *   <li>Toda acción relevante se registra automáticamente en el historial.</li>
 *   <li>El historial no puede modificarse desde afuera del agregado.</li>
 *   <li>Las transiciones de estado son siempre válidas.</li>
 *   <li>El estado y el historial están siempre sincronizados.</li>
 * </ul>
 *
 * <p>Las reglas de roles (quién puede ejecutar cada acción) son responsabilidad
 * del {@link GestorSolicitudService}, no de esta clase.</p>
 */
@Getter
public class Solicitud {

    /** Código único generado automáticamente al momento del registro. */
    private final CodigoSolicitud codigo;

    /** Descripción del motivo de la solicitud. Inmutable. */
    private final String descripcion;

    /** Usuario que registró la solicitud. Inmutable. */
    private final Usuario solicitante;

    /** Fecha y hora exacta en que fue registrada la solicitud. Inmutable. */
    private final LocalDateTime fechaCreacion;

    /** Tipo asignado durante la clasificación. */
    private TipoSolicitud tipoSolicitud;

    /** Prioridad calculada automáticamente durante la clasificación. */
    private Prioridad prioridad;

    /** Estado actual dentro del ciclo de vida. */
    private EstadoSolicitud estado;

    /** Administrativo asignado durante la revisión. */
    private Usuario responsable;

    /**
     * Historial cronológico de eventos. Usa {@code LinkedHashSet} para
     * garantizar orden de inserción y ausencia de duplicados.
     */
    private final Set<EventoHistorial> historial;

    /**
     * Registra una nueva solicitud en el sistema.
     * El estado inicial es {@link EstadoSolicitud#CLASIFICACION}.
     *
     * @param descripcion Motivo de la solicitud. No puede ser nulo ni vacío.
     * @param solicitante Usuario que registra. No puede ser nulo.
     * @throws ReglaDominioException si la descripción o el solicitante son inválidos.
     */
    public Solicitud(String descripcion, Usuario solicitante) {
        if (descripcion == null || descripcion.isBlank())
            throw new ReglaDominioException("La descripción no puede estar vacía");
        if (solicitante == null)
            throw new ReglaDominioException("El solicitante es obligatorio");

        this.codigo = CodigoSolicitud.generar();
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoSolicitud.CLASIFICACION;
        this.historial = new LinkedHashSet<>();

        registrarEvento(EventoHistorial.registrado(solicitante.getNombre()));
    }

    /**
     * Clasifica la solicitud asignándole un tipo y una prioridad.
     * Transiciona el estado de {@link EstadoSolicitud#CLASIFICACION}
     * a {@link EstadoSolicitud#PENDIENTE}.
     *
     * @param tipo      Tipo de la solicitud. No puede ser nulo.
     * @param prioridad Prioridad calculada por {@link AsignadorPrioridadService}. No puede ser nula.
     * @throws ReglaDominioException si el estado no es {@code CLASIFICACION},
     *                               o si el tipo o prioridad son nulos.
     */
    public void clasificar(TipoSolicitud tipo, Prioridad prioridad) {
        if (this.estado != EstadoSolicitud.CLASIFICACION)
            throw new ReglaDominioException("Solo se puede clasificar una solicitud en estado CLASIFICACION");
        if (tipo == null)
            throw new ReglaDominioException("El tipo de solicitud es obligatorio");
        if (prioridad == null)
            throw new ReglaDominioException("La prioridad es obligatoria");

        this.tipoSolicitud = tipo;
        this.prioridad = prioridad;
        this.estado = EstadoSolicitud.PENDIENTE;

        registrarEvento(EventoHistorial.clasificado("Sistema", tipo, prioridad));
    }

    /**
     * Pone la solicitud en revisión asignando un responsable.
     * Transiciona el estado de {@link EstadoSolicitud#PENDIENTE}
     * a {@link EstadoSolicitud#EN_PROCESO}.
     *
     * @param responsable Administrativo asignado. No puede ser nulo.
     * @throws ReglaDominioException si el estado no es {@code PENDIENTE}
     *                               o si el responsable es nulo.
     */
    public void enRevision(Usuario responsable) {
        if (this.estado != EstadoSolicitud.PENDIENTE)
            throw new ReglaDominioException("Solo se puede poner en revisión una solicitud PENDIENTE");
        if (responsable == null)
            throw new ReglaDominioException("El responsable es obligatorio para poner en revisión");

        this.responsable = responsable;
        this.estado = EstadoSolicitud.EN_PROCESO;

        registrarEvento(EventoHistorial.enRevision(responsable.getNombre()));
    }

    /**
     * Marca la solicitud como atendida por el responsable.
     * Transiciona el estado de {@link EstadoSolicitud#EN_PROCESO}
     * a {@link EstadoSolicitud#ATENDIDA}.
     *
     * @throws ReglaDominioException si el estado no es {@code EN_PROCESO}.
     */
    public void atendida() {
        if (this.estado != EstadoSolicitud.EN_PROCESO)
            throw new ReglaDominioException("Solo se puede atender una solicitud EN_PROCESO");

        this.estado = EstadoSolicitud.ATENDIDA;
        registrarEvento(EventoHistorial.atendido(responsable.getNombre()));
    }

    /**
     * Rechaza la solicitud durante la revisión.
     * Transiciona el estado de {@link EstadoSolicitud#EN_PROCESO}
     * a {@link EstadoSolicitud#RECHAZADA}.
     *
     * @throws ReglaDominioException si el estado no es {@code EN_PROCESO}.
     */
    public void rechazar() {
        if (this.estado != EstadoSolicitud.EN_PROCESO)
            throw new ReglaDominioException("Solo se puede rechazar una solicitud EN_PROCESO");

        this.estado = EstadoSolicitud.RECHAZADA;
        registrarEvento(EventoHistorial.rechazado(responsable.getNombre()));
    }

    /**
     * Cierra definitivamente la solicitud.
     * Transiciona el estado desde {@link EstadoSolicitud#ATENDIDA} o
     * {@link EstadoSolicitud#RECHAZADA} a {@link EstadoSolicitud#CERRADA}.
     *
     * @throws ReglaDominioException si el estado no es {@code ATENDIDA} ni {@code RECHAZADA}.
     */
    public void cerrar() {
        if (this.estado != EstadoSolicitud.ATENDIDA && this.estado != EstadoSolicitud.RECHAZADA)
            throw new ReglaDominioException("Solo se puede cerrar una solicitud ATENDIDA o RECHAZADA");

        this.estado = EstadoSolicitud.CERRADA;
        registrarEvento(EventoHistorial.cerrado(
                responsable != null ? responsable.getNombre() : "Sistema"
        ));
    }

    /**
     * Cancela la solicitud. Solo puede ejecutarla el propio solicitante
     * y únicamente cuando la solicitud está en estado {@link EstadoSolicitud#PENDIENTE}.
     *
     * @param quien Usuario que intenta cancelar la solicitud.
     * @throws ReglaDominioException si el estado no es {@code PENDIENTE}
     *                               o si quien cancela no es el solicitante original.
     */
    public void cancelar(Usuario quien) {
        if (this.estado != EstadoSolicitud.PENDIENTE)
            throw new ReglaDominioException("Solo se puede cancelar una solicitud PENDIENTE");
        if (!quien.getId().equals(this.solicitante.getId()))
            throw new ReglaDominioException("Solo el solicitante puede cancelar su propia solicitud");

        this.estado = EstadoSolicitud.CANCELADA;
        registrarEvento(EventoHistorial.cancelado(quien.getNombre()));
    }

    /**
     * Retorna una vista inmutable del historial de eventos de la solicitud.
     * Garantiza que ninguna clase externa pueda modificar el historial.
     *
     * @return Lista inmutable de eventos en orden cronológico.
     */
    public List<EventoHistorial> getHistorial() {
        return Collections.unmodifiableList(new ArrayList<>(historial));
    }

    /**
     * Registra un nuevo evento en el historial.
     * Es privado para garantizar que solo el agregado pueda agregar eventos.
     *
     * @param evento Evento a registrar.
     */
    private void registrarEvento(EventoHistorial evento) {
        this.historial.add(evento);
    }
}