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
 * Caso de uso para poner una solicitud en revisión asignando un responsable.
 *
 * <p>Verifica que tanto la solicitud como el responsable existan, delega
 * la lógica de rol y estado al {@link GestorSolicitudService} y notifica
 * al responsable de su asignación.</p>
 */
@Service
@RequiredArgsConstructor
public class EnRevisionUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;
    private final NotificacionService notificacionService;

    /**
     * Pone la solicitud en revisión asignando el responsable indicado.
     *
     * @param codigo        Código único de la solicitud a poner en revisión.
     * @param responsableId Identificador del administrativo asignado como responsable.
     * @return Solicitud actualizada en estado EN_PROCESO.
     * @throws SolicitudNoEncontradaException si no existe la solicitud.
     * @throws UsuarioNoEncontradoException   si no existe el responsable.
     */
    @Transactional
    public Solicitud ejecutar(String codigo, String responsableId) {
        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));

        Usuario responsable = usuarioRepository.findById(responsableId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(responsableId));

        gestor.enRevision(solicitud, responsable);
        Solicitud guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarResponsableAsignado(guardada, responsable);
        return guardada;
    }
}