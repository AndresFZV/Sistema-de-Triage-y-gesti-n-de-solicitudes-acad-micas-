package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso para cerrar definitivamente una solicitud.
 *
 * <p>El cierre es la última transición del ciclo de vida y solo es posible
 * desde los estados ATENDIDA o RECHAZADA. Solo un administrativo puede
 * ejecutar esta acción. La validación de rol y estado es delegada al
 * {@link GestorSolicitudService} y al agregado {@code Solicitud}.</p>
 */
@Service
@RequiredArgsConstructor
public class CerrarSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;
    private final NotificacionService notificacionService;

    /**
     * Cierra la solicitud indicada por el administrativo.
     *
     * @param codigo  Código único de la solicitud a cerrar.
     * @param adminId Identificador del administrativo que ejecuta el cierre.
     * @return Solicitud actualizada en estado CERRADA.
     * @throws SolicitudNoEncontradaException si no existe la solicitud.
     * @throws UsuarioNoEncontradoException   si no existe el administrativo.
     */
    @Transactional
    public Solicitud ejecutar(String codigo, String adminId) {
        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(adminId));

        gestor.cerrar(solicitud, admin);
        Solicitud guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudCerrada(guardada, solicitud.getSolicitante());
        return guardada;
    }
}