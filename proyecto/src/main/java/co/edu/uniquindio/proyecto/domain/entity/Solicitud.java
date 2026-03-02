package co.edu.uniquindio.proyecto.domain.entity;

import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.*;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    // El historial es privado y solo se modifica a través de la raíz
    private final List<EventoHistorial> historial;

    public Solicitud(String descripcion, Usuario solicitante) {
        if (descripcion == null || descripcion.isBlank())
            throw new ReglaDominioException("La descripción no puede estar vacía");
        if (solicitante == null)
            throw new ReglaDominioException("El solicitante es obligatorio");
        if (!solicitante.esEstudiante())
            throw new ReglaDominioException("Solo un estudiante puede registrar una solicitud");

        this.codigo = CodigoSolicitud.generar();
        this.descripcion = descripcion;
        this.solicitante = solicitante;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoSolicitud.PENDIENTE;
        this.historial = new ArrayList<>();

        // Registrar el primer evento automáticamente
        registrarEvento(EventoHistorial.registrado(solicitante.getNombre()));
    }

    public void clasificar(TipoSolicitud tipo, Prioridad prioridad, Usuario responsable) {
        if (estaCerrada())
            throw new ReglaDominioException("Una solicitud cerrada no puede modificarse");
        if (!responsable.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede clasificar una solicitud");
        if (tipo == null)
            throw new ReglaDominioException("El tipo de solicitud es obligatorio");
        if (prioridad == null)
            throw new ReglaDominioException("La prioridad es obligatoria");

        this.tipoSolicitud = tipo;
        this.prioridad = prioridad;
        this.responsable = responsable;
        this.estado = EstadoSolicitud.EN_PROCESO;

        registrarEvento(EventoHistorial.clasificado(responsable.getNombre(), tipo, prioridad));
    }

    public void marcarComoAtendida() {
        if (this.estado != EstadoSolicitud.EN_PROCESO)
            throw new ReglaDominioException("Solo se puede atender una solicitud que esté en proceso");
        if (this.responsable == null)
            throw new ReglaDominioException("No se puede atender una solicitud sin responsable");

        this.estado = EstadoSolicitud.ATENDIDA;

        registrarEvento(EventoHistorial.atendido(responsable.getNombre()));
    }

    public void cerrar() {
        if (estaCerrada())
            throw new ReglaDominioException("La solicitud ya está cerrada");
        if (this.responsable == null)
            throw new ReglaDominioException("No se puede cerrar una solicitud sin responsable asignado");
        if (this.estado != EstadoSolicitud.ATENDIDA)
            throw new ReglaDominioException("No se puede cerrar una solicitud que no ha sido atendida");

        this.estado = EstadoSolicitud.CERRADA;

        registrarEvento(EventoHistorial.cerrado(responsable.getNombre()));
    }

    public void rechazar() {
        if (estaCerrada())
            throw new ReglaDominioException("Una solicitud cerrada no puede modificarse");

        String quien = responsable != null ? responsable.getNombre() : "Sistema";
        this.estado = EstadoSolicitud.RECHAZADA;

        registrarEvento(EventoHistorial.rechazado(quien));
    }

    // Devuelve una copia inmutable del historial para que nadie lo modifique desde afuera
    public List<EventoHistorial> getHistorial() {
        return Collections.unmodifiableList(historial);
    }

    // Método privado: solo la raíz puede registrar eventos
    private void registrarEvento(EventoHistorial evento) {
        this.historial.add(evento);
    }

    private boolean estaCerrada() {
        return this.estado == EstadoSolicitud.CERRADA
                || this.estado == EstadoSolicitud.RECHAZADA;
    }
}