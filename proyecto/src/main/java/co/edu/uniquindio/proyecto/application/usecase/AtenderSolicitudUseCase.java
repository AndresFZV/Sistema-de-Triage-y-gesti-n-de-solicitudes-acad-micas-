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
 * Caso de uso para marcar una solicitud como atendida.
 *
 * <p>Orquesta la validación de existencia del administrativo y la solicitud,
 * delega la lógica de rol al {@link GestorSolicitudService} y notifica
 * al solicitante una vez confirmada la atención.</p>
 */
@Service
@RequiredArgsConstructor
public class AtenderSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;
    private final NotificacionService notificacionService;

    /**
     * Marca la solicitud como atendida por el administrativo indicado.
     *
     * @param codigo  Código único de la solicitud a atender.
     * @param adminId Identificador del administrativo que ejecuta la acción.
     * @return Solicitud actualizada en estado ATENDIDA.
     * @throws SolicitudNoEncontradaException si no existe la solicitud.
     * @throws UsuarioNoEncontradoException   si no existe el administrativo.
     */
    @Transactional
    public Solicitud ejecutar(String codigo, String adminId) {
        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));

        Usuario admin = usuarioRepository.findById(adminId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(adminId));

        gestor.atendida(solicitud, admin);
        Solicitud guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudAtendida(guardada, solicitud.getSolicitante());
        return guardada;
    }
}