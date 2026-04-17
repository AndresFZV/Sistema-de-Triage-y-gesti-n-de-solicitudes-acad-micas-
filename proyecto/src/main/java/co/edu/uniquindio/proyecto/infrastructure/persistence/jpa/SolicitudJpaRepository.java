package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.domain.entity.Solicitud;
import co.edu.uniquindio.proyecto.domain.entity.Usuario;
import co.edu.uniquindio.proyecto.domain.exception.UsuarioNoEncontradoException;
import co.edu.uniquindio.proyecto.domain.repository.SolicitudRepository;
import co.edu.uniquindio.proyecto.domain.repository.UsuarioRepository;
import co.edu.uniquindio.proyecto.domain.valueobject.EstadoSolicitud;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.EstadoSolicitudEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.SolicitudEntity;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.mapper.SolicitudPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SolicitudJpaRepository implements SolicitudRepository {

    private final SolicitudJpaDataRepository dataRepository;
    private final SolicitudPersistenceMapper mapper;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public Solicitud save(Solicitud solicitud) {
        SolicitudEntity entity = mapper.toEntity(solicitud);
        SolicitudEntity saved = dataRepository.save(entity);
        return toDomainWithRelations(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Solicitud> findById(String id) {
        return dataRepository.findByCodigo(id)
                .map(this::toDomainWithRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Solicitud> findByCodigo(String codigo) {
        return dataRepository.findByCodigo(codigo)
                .map(this::toDomainWithRelations);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findByEstado(EstadoSolicitud estado) {
        EstadoSolicitudEnum estadoEnum = EstadoSolicitudEnum.valueOf(estado.name());
        return dataRepository.findByEstado(estadoEnum).stream()
                .map(this::toDomainWithRelations)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Solicitud> findAll() {
        return dataRepository.findAll().stream()
                .map(this::toDomainWithRelations)
                .collect(Collectors.toList());
    }

    /**
     * Ejercicio 4 — Paginación:
     * Devuelve una página de solicitudes con estado distinto a CANCELADA,
     * ordenadas por fecha de creación descendente.
     *
     * @param pagina  Número de página (0-indexed)
     * @param tamano  Cantidad de elementos por página
     * @return Lista de solicitudes del dominio correspondiente a la página solicitada
     */
    @Transactional(readOnly = true)
    public List<Solicitud> findActivasPaginadas(int pagina, int tamano) {
        Pageable pageable = PageRequest.of(pagina, tamano, Sort.by("fechaCreacion").descending());
        Page<SolicitudEntity> resultado = dataRepository.findByEstadoNot(
                EstadoSolicitudEnum.CANCELADA, pageable
        );
        return resultado.getContent().stream()
                .map(this::toDomainWithRelations)
                .collect(Collectors.toList());
    }

    private Solicitud toDomainWithRelations(SolicitudEntity entity) {
        Usuario solicitante = usuarioRepository
                .findById(entity.getSolicitanteCodigo())
                .orElseThrow(() -> new UsuarioNoEncontradoException(entity.getSolicitanteCodigo()));

        Usuario responsable = null;
        if (entity.getResponsableCodigo() != null) {
            responsable = usuarioRepository
                    .findById(entity.getResponsableCodigo())
                    .orElse(null);
        }

        return mapper.toDomain(entity, solicitante, responsable);
    }
}