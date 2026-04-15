package co.edu.uniquindio.proyecto.infrastructure.persistence.jpa;

import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.EstadoSolicitudEnum;
import co.edu.uniquindio.proyecto.infrastructure.persistence.jpa.entity.SolicitudEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface SolicitudJpaDataRepository extends JpaRepository<SolicitudEntity, Long> {

    Optional<SolicitudEntity> findByCodigo(String codigo);

    List<SolicitudEntity> findByEstado(EstadoSolicitudEnum estado);
}