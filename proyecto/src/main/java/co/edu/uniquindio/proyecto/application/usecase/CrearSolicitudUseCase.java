package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.service.NotificacionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;
    private final NotificacionService notificacionService;

    @Transactional
    public Solicitud ejecutar(String descripcion, String solicitanteId) {
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(solicitanteId));

        Solicitud solicitud = gestor.registrar(descripcion, solicitante);
        Solicitud guardada = solicitudRepository.save(solicitud);
        notificacionService.notificarSolicitudCreada(guardada, solicitante);
        return guardada;
    }
}