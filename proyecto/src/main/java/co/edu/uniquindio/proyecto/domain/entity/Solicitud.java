package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Solicitud {

    private final CodigoSolicitud codigo;
    private final String descripcion;
    private final Usuario solicitante;
    private final LocalDateTime fechaCreacion;

    private TipoSolicitud tipoSolicitud;
    private Prioridad prioridad;
    private EstadoSolicitud estado;
    private Usuario responsable;

    private final Set<EventoHistorial> historial;

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

    public void enRevision(Usuario responsable) {
        if (this.estado != EstadoSolicitud.PENDIENTE)
            throw new ReglaDominioException("Solo se puede poner en revisión una solicitud PENDIENTE");
        if (responsable == null)
            throw new ReglaDominioException("El responsable es obligatorio para poner en revisión");

        this.responsable = responsable;
        this.estado = EstadoSolicitud.EN_PROCESO;

        registrarEvento(EventoHistorial.enRevision(responsable.getNombre()));
    }

    public void atendida() {
        if (this.estado != EstadoSolicitud.EN_PROCESO)
            throw new ReglaDominioException("Solo se puede atender una solicitud EN_PROCESO");

        this.estado = EstadoSolicitud.ATENDIDA;
        registrarEvento(EventoHistorial.atendido(responsable.getNombre()));
    }

    public void rechazar() {
        if (this.estado != EstadoSolicitud.EN_PROCESO)
            throw new ReglaDominioException("Solo se puede rechazar una solicitud EN_PROCESO");

        this.estado = EstadoSolicitud.RECHAZADA;
        registrarEvento(EventoHistorial.rechazado(responsable.getNombre()));
    }

    public void cerrar() {
        if (this.estado != EstadoSolicitud.ATENDIDA && this.estado != EstadoSolicitud.RECHAZADA)
            throw new ReglaDominioException("Solo se puede cerrar una solicitud ATENDIDA o RECHAZADA");

        this.estado = EstadoSolicitud.CERRADA;
        registrarEvento(EventoHistorial.cerrado(
                responsable != null ? responsable.getNombre() : "Sistema"
        ));
    }

    public void cancelar(Usuario quien) {
        if (this.estado != EstadoSolicitud.PENDIENTE)
            throw new ReglaDominioException("Solo se puede cancelar una solicitud PENDIENTE");
        if (!quien.getId().equals(this.solicitante.getId()))
            throw new ReglaDominioException("Solo el solicitante puede cancelar su propia solicitud");

        this.estado = EstadoSolicitud.CANCELADA;
        registrarEvento(EventoHistorial.cancelado(quien.getNombre()));
    }

    public List<EventoHistorial> getHistorial() {
        return Collections.unmodifiableList(new ArrayList<>(historial));
    }

    private void registrarEvento(EventoHistorial evento) {
        this.historial.add(evento);
    }
}