package co.edu.uniquindio.proyecto.aplication.usecase;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.service.GestorSolicitudService;
import co.edu.uniquindio.proyecto.domain.valueobject.CodigoSolicitud;
import co.edu.uniquindio.proyecto.domain.valueobject.TipoSolicitud;

public class ClasificarSolicitudUseCase {
    private final SolicitudRepository solicitudRepository;
    private final UsuarioRepository usuarioRepository;
    private final GestorSolicitudService gestor;

    public ClasificarSolicitudUseCase(SolicitudRepository solicitudRepository,
                                      UsuarioRepository usuarioRepository,
                                      GestorSolicitudService gestor) {
        this.solicitudRepository = solicitudRepository;
        this.usuarioRepository = usuarioRepository;
        this.gestor = gestor;
    }

    public void ejecutar(String codigo, TipoSolicitud tipo, String adminId) {

        Solicitud solicitud = solicitudRepository.obtenerPorCodigo(new CodigoSolicitud(codigo));
        Usuario admin = usuarioRepository.obtenerPorId(adminId);

        gestor.clasificar(solicitud, tipo, admin);

        solicitudRepository.guardar(solicitud);
    }
}