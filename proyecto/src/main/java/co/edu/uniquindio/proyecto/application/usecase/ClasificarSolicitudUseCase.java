package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.service.NotificacionService;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para clasificar una solicitud asignándole un tipo.
 *
 * <p>Durante la clasificación, la prioridad es calculada automáticamente
 * por el sistema con base en el tipo y el tiempo transcurrido desde la
 * creación. Solo un administrativo puede ejecutar esta acción sobre una
 * solicitud en estado CLASIFICACION.</p>
 */
@Service
@RequiredArgsConstructor
public class ClasificarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;
    private final NotificacionService notificacionService;

    /**
     * Clasifica la solicitud asignándole el tipo indicado.
     *
     * @param codigo  Código único de la solicitud a clasificar.
     * @param tipo    Tipo de solicitud asignado (ej. HOMOLOGACION, CANCELACION).
     * @param adminId Identificador del administrativo que ejecuta la clasificación.
     * @return Solicitud actualizada en estado PENDIENTE con tipo y prioridad asignados.
     * @throws SolicitudNoEncontradaException si no existe la solicitud.
     * @throws UsuarioNoEncontradoException   si no existe el administrativo.
     */
    @Transactional
    public Solicitud ejecutar(String codigo, TipoSolicitud tipo, String adminId) {
        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(adminId));

        gestor.clasificar(solicitud, tipo, admin);
        Solicitud guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudClasificada(guardada, solicitud.getSolicitante());
        return guardada;
    }
}