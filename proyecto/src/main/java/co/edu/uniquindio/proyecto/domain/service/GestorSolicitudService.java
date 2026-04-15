package co.edu.uniquindio.proyecto.domain.service;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.ReglaDominioException;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.Prioridad;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servicio de dominio que gestiona las reglas de roles sobre el ciclo de vida
 * de las solicitudes.
 *
 * <p>Su responsabilidad es validar <b>quién puede ejecutar cada acción</b>,
 * separando esta preocupación del agregado {@link Solicitud}, que solo
 * valida <b>desde qué estado</b> se puede ejecutar cada acción.</p>
 *
 *
 * <p>Reglas de roles:</p>
 * <ul>
 *   <li>Cualquier usuario puede registrar una solicitud.</li>
 *   <li>Solo un administrativo puede clasificar, poner en revisión,
 *       atender, rechazar y cerrar.</li>
 *   <li>Solo el solicitante puede cancelar su propia solicitud.</li>
 * </ul>
 */

@Service
@RequiredArgsConstructor
public class GestorSolicitudService {

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


    public void clasificar(Solicitud solicitud, TipoSolicitud tipo, Usuario quien) {
        if (!quien.esAdministrativo())
            throw new ReglaDominioException("Solo un administrativo puede clasificar una solicitud");

        // Ya no necesita AsignadorPrioridadService
        Prioridad prioridad = Prioridad.calcular(tipo, solicitud.getFechaCreacion());
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