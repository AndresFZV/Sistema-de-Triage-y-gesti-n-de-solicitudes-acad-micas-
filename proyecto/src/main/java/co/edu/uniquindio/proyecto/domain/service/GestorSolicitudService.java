package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;

/**
 * Servicio de dominio que gestiona las reglas de roles sobre el ciclo de vida
 * de las solicitudes.
 *
 * <p>Su responsabilidad es validar <b>quién puede ejecutar cada acción</b>,
 * separando esta preocupación del agregado {@link Solicitud}, que solo
 * valida <b>desde qué estado</b> se puede ejecutar cada acción.</p>
 *
 * <p>Coordina el uso de {@link AsignadorPrioridadService} para calcular
 * automáticamente la prioridad durante la clasificación.</p>
 *
 * <p>Reglas de roles:</p>
 * <ul>
 *   <li>Cualquier usuario puede registrar una solicitud.</li>
 *   <li>Solo un administrativo puede clasificar, poner en revisión,
 *       atender, rechazar y cerrar.</li>
 *   <li>Solo el solicitante puede cancelar su propia solicitud.</li>
 * </ul>
 */
public class GestorSolicitudService {

    /** Servicio que calcula la prioridad automáticamente. */
    private final AsignadorPrioridadService asignadorPrioridad;

    /**
     * Crea una nueva instancia del gestor con su servicio de prioridad.
     */
    public GestorSolicitudService() {
        this.asignadorPrioridad = new AsignadorPrioridadService();
    }

    /**
     * Registra una nueva solicitud. Cualquier usuario puede registrar.
     *
     * @param descripcion Motivo de la solicitud.
     * @param solicitante Usuario que registra la solicitud.
     * @return La solicitud creada en estado {@link EstadoSolicitud#CLASIFICACION}.
     */
    public Solicitud registrar(String descripcion, Usuario solicitante) {
        return new Solicitud(descripcion, solicitante);
    }

    /**
     * Clasifica una solicitud asignándole un tipo. La prioridad es calculada
     * automáticamente por {@link AsignadorPrioridadService}.
     *
     * @param solicitud Solicitud a clasificar.
     * @param tipo      Tipo asignado a la solicitud.
     * @param quien     Usuario que ejecuta la acción. Debe ser administrativo.
     * @throws ReglaDominioException si quien clasifica no es administrativo.
     */
    public void clasificar(Solicitud solicitud, TipoSolicitud tipo, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede clasificar una solicitud");

        Prioridad prioridad = asignadorPrioridad.asignar(tipo, solicitud.getFechaCreacion());
        solicitud.clasificar(tipo, prioridad);
    }

    /**
     * Pone una solicitud en revisión asignando un responsable.
     *
     * @param solicitud   Solicitud a poner en revisión.
     * @param responsable Administrativo asignado como responsable.
     * @throws ReglaDominioException si el responsable no es administrativo.
     */
    public void enRevision(Solicitud solicitud, Usuario responsable) {
        if (!responsable.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede poner en revisión una solicitud");

        solicitud.enRevision(responsable);
    }

    /**
     * Marca una solicitud como atendida.
     *
     * @param solicitud Solicitud a marcar como atendida.
     * @param quien     Usuario que ejecuta la acción. Debe ser administrativo.
     * @throws ReglaDominioException si quien atiende no es administrativo.
     */
    public void atendida(Solicitud solicitud, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede marcar una solicitud como atendida");

        solicitud.atendida();
    }

    /**
     * Rechaza una solicitud durante la revisión.
     *
     * @param solicitud Solicitud a rechazar.
     * @param quien     Usuario que ejecuta la acción. Debe ser administrativo.
     * @throws ReglaDominioException si quien rechaza no es administrativo.
     */
    public void rechazar(Solicitud solicitud, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede rechazar una solicitud");

        solicitud.rechazar();
    }

    /**
     * Cierra definitivamente una solicitud.
     *
     * @param solicitud Solicitud a cerrar.
     * @param quien     Usuario que ejecuta la acción. Debe ser administrativo.
     * @throws ReglaDominioException si quien cierra no es administrativo.
     */
    public void cerrar(Solicitud solicitud, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede cerrar una solicitud");

        solicitud.cerrar();
    }

    /**
     * Cancela una solicitud. La validación de que solo el solicitante puede
     * cancelar su propia solicitud es delegada al agregado {@link Solicitud}.
     *
     * @param solicitud Solicitud a cancelar.
     * @param quien     Usuario que intenta cancelar.
     */
    public void cancelar(Solicitud solicitud, Usuario quien) {
        solicitud.cancelar(quien);
    }
}