package co.edu.uniquindio.proyecto.application.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ConsultarUsuariosUseCase {

    private final UsuarioRepository usuarioRepository;
    private final SolicitudRepository solicitudRepository;

    @Transactional(readOnly = true)
    public List<Usuario> ejecutar() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> ejecutarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> ejecutarSolicitudesPorUsuario(String usuarioId) {
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(usuarioId));
        return solicitudRepository.findBySolicitanteId(usuarioId);
    }
}