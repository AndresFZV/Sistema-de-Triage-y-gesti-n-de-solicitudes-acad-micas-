package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.SolicitudNoEncontradaException;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnRevisionUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;

    @Transactional
    public Solicitud ejecutar(String codigo, String responsableId) {
        Solicitud solicitud = solicitudRepository.findByCodigo(codigo)
                .orElseThrow(() -> new SolicitudNoEncontradaException(codigo));

        Usuario responsable = usuarioRepository.findById(responsableId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(responsableId));

        gestor.enRevision(solicitud, responsable);
        return solicitudRepository.save(solicitud);
    }
}